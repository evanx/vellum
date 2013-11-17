
var server = {
   accessToken: '',
   post: function(req) {
      console.log('post', req.url);
      req.type = 'POST';
      if (!req.error) {
         req.error = function() {
            console.log('error', req.url);
         }
      }
      $.ajax(req);
   }
};

var unloadedCount = 1;
var tbodyHtml = {};
var state = {}; 

function documentReady() {
   console.log('documentReady');
   $('.page-all').hide();
   $('.page-all').removeClass('hide');
   $('#page-login').show();
   $('#input-username').focus();
   $('#button-login').click(login);
   $('form.search').find('button[name=search]').click(search);
   $('#nav-search').click(navigateSearch);
   $('#nav-connections').click(navigateConnections);
   $('#connection-cancel').click(cancelConnectionForm);
   $('#connection-save').click(saveConnectionForm);
   $('#button-newConnection').click(newConnection);
   tbodyHtml.connections = $('#tbody-connections').html();
   tbodyHtml.results = $('#tbody-results').html();
   getConnections();
}

function load() {
   $('#page-connection').load('connection.html', function() {
      console.log('loaded', name);
      unloadedCount--;
   });

}
function login() {
   $('.page-all').hide();
   $('#page-search').show();
   var username = $('#input-username').val();
   console.log('login', username);
}

function search() {
   var data = $('form.search').serialize();
   console.log('search', data);
   $.ajax({
      type: 'POST',
      url: '/app/search',
      data: data,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
            state.results = results;
            if (res.length > 0) {
               showResults(res);
            }
         }
      },
      error: function() {
         console.log('error');
      }
   });   
}

function showResults(results) {
   console.log('showResults', results.length);
   state.results = results;
   $('#tbody-results').empty();
   for (var i = 0; i < results.length; i++) {
      console.log("row", i, results[i]);
      $('#tbody-results').append(tbodyHtml.results);
      var tr = $("#tbody-results > tr:last-child");
      tr.find('span.td-connection').text(results[i].connectionName);
      tr.find('span.td-table').text(results[i].tableName);
      tr.find('span.td-column').text(results[i].columnName);
      tr.find('span.td-row').text(results[i].rowId);
      tr.find('span.td-content').text(results[i].content.substring(0, 30));
      tr.click(results[i], function(event) {
         showResult(event.data);
      });
   }
   $('.page-all').hide();
   $('#page-results').show();
}

function showResult(result) {
   console.log('showResult', result);
   $('#result-connectionName').text(result.connectionName);
   $('#result-tableName').text(result.tableName);
   $('#result-columnName').text(result.columnName);
   $('#result-rowId').text(result.rowId);
   $('#result-content').text(result.content);
   $('.page-all').hide();
   $('#page-result').show();   
}

function navigateSearch() {
   var options = $('form.search').find('select[name=connection]');
   options.empty();
   $.each(state.connections, function() {
      options.append($("<option />").val(this.connectionName).text(this.connectionName));
   });
   console.log('linkSearch');
   enableSearch();
   $('form.search').find('input[name=search]').on('keyup', function() {
      enableSearch();
   });
   $('.nav-all').removeClass('active');
   $('#nav-search').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-search').show();
   $('form.search').find('input[name=search]').focus();
   return false;
}

function enableSearch() {
   if ($('form.search').find('input[name=search]').val()) {
      $('form.search').find('button[name=search]').removeAttr('disabled');
   } else {
      $('form.search').find('button[name=search]').attr('disabled', 'disabled');
   }

function navigateResults(event) {
   console.log('navigateResults');
   if (state.results) {
      showResults(state.results);
   }
   return false;
}
}

function navigateConnections(event) {
   console.log('navigateConnections');
   showConnections();
   return false;
}

function getConnections() {
   $.ajax({
      type: 'POST',
      data: '',
      url: '/app/connection/list',
      success: function(res) {
         console.log("list", res.length);
         if (res.error) {
         } else {
            state.connections = res;
         }
      }
   });
}

function showConnections() {
   console.log('showConnections');
   $('.nav-all').removeClass('active');
   $('#nav-connections').parent('li').addClass('active');
   $('#tbody-connections').empty();
   $('.page-all').hide();
   $('#page-connections').show();
   $.ajax({
      type: 'POST',
      data: '',
      url: '/app/connection/list',
      success: function(res) {
         console.log("list", res.length);
         if (res.error) {
         } else {
            state.connections = res;
            for (var i = 0; i < res.length; i++) {
               console.log("row", i, res[i]);
               $('#tbody-connections').append(tbodyHtml.connections);
               var tr = $("#tbody-connections > tr:last-child");
               tr.find('span.td-name').text(res[i].connectionName);
               tr.find('span.td-driver').text(res[i].driver);
               tr.find('span.td-url').text(res[i].url);
               tr.find('span.td-user').text(res[i].user);
               tr.find('button.button-edit').click(res[i], function(event) {
                  console.log('edit', event.data);
                  editConnection(event.data);
                  return false;
               });
               tr.find('button.button-remove').click(res[i], function(event) {
                  console.log('remove', event.data);
                  $(this).closest('tr').remove();
                  deleteConnection(event.data);
                  return false;
               });
               tr.click(res[i], function(event) {
                  editConnection(event.data);
               });
            }
         }
      },
   });
}

function newConnection() {
   console.log('newConnection');
   state.connection = null;
   resetConnectionForm();
   $('form.connection').find('input[name=connectionName]').removeAttr('disabled');
   $('form.connection').find('input[name=connectionName]').focus();
   return false;
}

function editConnection(connection) {
   console.log('editConnection', connection);
   state.connection = connection;
   resetConnectionForm();
   $('form.connection').find('input[name=connectionName]').attr('disabled', 'disabled');
   $('form.connection').find('input[name=driver]').focus();
   $('form.connection').find('input[name=connectionName]').val(connection.connectionName);
   $('form.connection').find('input[name=driver]').val(connection.driver);
   $('form.connection').find('input[name=url]').val(connection.url);
   $('form.connection').find('input[name=user]').val(connection.user);
   $('form.connection').find('input[name=password]').val(connection.password);
}

function resetConnectionForm() {
   console.log('resetConnectionForm');
   $('.page-all').hide();
   $('#page-connection').show();
   $('form.connection').find('input').val('');
   $('form.connection').children('div').removeClass('has-error');
   $('form.connection input').first().focus();
}

function deleteConnection(connection) {
   console.log('deleteConnection', connection);
   $.ajax({
      type: 'POST',
      url: '/app/connection/delete/' + connection.connectionName,
      data: connection.connectionName,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
         }
      }
   });
   return false;
}

function cancelConnectionForm(event) {
   console.log('cancelConnectionForm');
   resetConnectionForm();
   showConnections();
   return false;
}

function saveConnectionForm(event) {
   var data = $('form.connection').serialize();
   console.log("saveConnectionForm", data);
   if (validateFilled($('form.connection'))) {
      if (state.connection) {
         updateConnection(data);
      } else {
         insertConnection(data);
      }
   }
   return false;
}

function validateFilled(form) {
   var ok = true;
   form.children().removeClass('has-error');
   form.find('input').each(function() {
      console.log("validate", this, $(this).val());
      if (ok && !$(this).val()) {
         ok = false;
         $(this).parent('div.form-group').addClass('has-error');
         $(this).focus();
      }
   });
   return true;
}

function insertConnection(data) {
   console.log('insertConnection', data);
   $.ajax({
      type: 'POST',
      url: '/app/connection/insert',
      data: data,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
            showConnections();
         }
      },
      error: function() {
         console.log('error');
      }
   });
}

function updateConnection(data) {
   console.log('updateConnection', data);
   $.ajax({
      type: 'POST',
      url: '/app/connection/update/' + state.connection.connectionName,
      data: data,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
            showConnections();
         }
      },
      error: function() {
         console.log('error');
      }
   });
}
