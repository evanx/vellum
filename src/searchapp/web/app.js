
var server = {
   accessToken: 'testAccessToken',
   post: function(req) {
      req.type = 'POST';
      if (!req.data) {
         req.data = 'accessToken=' + server.accessToken;
      }
      if (!req.error) {
         req.error = function() {
            console.log('error');
         }
      }
      $.ajax(req);
   }
};

var tbodyHtml = {};

var unloadedCount = 1;

function documentReady() {
   console.log('documentReady');
   $('.page-all').hide();
   $('.page-all').removeClass('hide');
   $('#page-login').show();
   $('#input-username').focus();
   $('#button-login').click(login);
   $('#button-search').click(search);
   $('#link-search').click(showSearch);
   $('#link-connections').click(showConnections);
   tbodyHtml.connections = $('#tbody-connections').html();
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
}

function showSearch() {
   console.log('linkSearch');
   $('.link-all').removeClass('active');
   $('#link-search').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-search').show();
}

function showConnections() {
   console.log('linkConnections');
   $('.link-all').removeClass('active');
   $('#link-connections').parent('li').addClass('active');
   $('#page-connection').find('button.button-cancel').click(cancelConnectionForm);
   $('#page-connection').find('button.button-save').click(saveConnectionForm);
   $('#button-newConnection').click(newConnection);
   $('.page-all').hide();
   $('#page-connections').show();
   $('#tbody-connections').empty();
   server.post({
      url: '/app/connection/list',
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
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
                  deleteConnection(event.data);
                  $(this).closest('tr').remove();
                  return false;
               });
               tr.click(res[i], function(event) {
                  console.log('row', event);
                  editConnection(event.data);
                  return false;
               });
            }
         }
      },
   });
}

function newConnection() {
   console.log('newConnection');
   resetConnectionForm();
}

function editConnection(connection) {
   console.log('editConnection', connection);
   resetConnectionForm();
   $('form.connection').find('input[name=connectionName]').val(connection.connectionName);
   $('form.connection').find('input[name=driver]').val(connection.driver);
   $('form.connection').find('input[name=url]').val(connection.url);
   $('form.connection').find('input[name=user]').val(connection.user);
   $('form.connection').find('input[name=password]').val(connection.password);
}

function resetConnectionForm() {
   console.log('newConnection');
   $('.page-all').hide();
   $('#page-connection').show();
   $('form.connection').find('input').val('');
   $('form.connection').children('div').removeClass('has-error');
   $('form.connection').submit(function() {
      return false;
   });
   $('form.connection input').first().focus();
}


function deleteConnection(connection) {
   console.log('deleteConnection', connection);
   server.post({
      url: '/app/connection/delete/' + connection.connectionName,
      data: connection.connectionName,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {

         }
      }
   });
}

function cancelConnectionForm() {
   console.log('cancelConnection');
   showConnections();
}

function saveConnectionForm() {
   var data = $('form.connection').serialize();
   console.log("saveConnectionForm", data);
   if (validateFilled($('form.connection'))) {
      saveConnection(data);
   }
}

function validateFilled(form) {
   var ok = true;
   form.children().removeClass('has-error');
   form.find('input').each(function() {
      console.log("validate", this, $(this).val());
      if (!$(this).val()) {
         $(this).parent('div.form-group').addClass('has-error');
         if (ok) {
            $(this).focus();
            ok = false;
         }
      }      
   });
   return ok;
}

function saveConnection(connection) {
   console.log('saveConnection', connection);
   server.post({
      url: '/app/connection/save',
      data: connection,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
         }
      }
   });
}
