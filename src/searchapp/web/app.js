
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
   $('#button-search').click(search);
   $('#link-search').click(navigateSearch);
   $('#link-connections').click(navigateConnections);
   $('#connection-cancel').click(cancelConnectionForm);
   $('#connection-save').click(saveConnectionForm);
   $('#button-newConnection').click(newConnection);
   tbodyHtml.connections = $('#tbody-connections').html();
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
   var searchVal = $('#input-search').val();
   console.log('search', searchVal);
   $.ajax({
      type: 'POST',
      url: '/app/search',
      data: searchVal,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
         }
      },
      error: function() {
         console.log('error');
      }
   });   
}

function navigateSearch() {
   var options = $('#select-connection');
   $.each(state.connections, function() {
      options.append($("<option />").val(this.connectionName).text(this.connectionName));
   });
   console.log('linkSearch');
   $('.link-all').removeClass('active');
   $('#link-search').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-search').show();
   return false;
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
   $('.link-all').removeClass('active');
   $('#link-connections').parent('li').addClass('active');
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
                  console.log('row', event);
                  editConnection(event.data);
               });
            }
         }
      },
   });
   return false;
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
