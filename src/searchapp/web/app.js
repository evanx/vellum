
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

function documentReady() {
   console.log('documentReady');
   $('.page-all').hide();
   $('.page-all').removeClass('hide');
   $('#page-login').show();
   $('#input-username').focus();
   $('#button-login').click(loginSubmit);
   $('#button-search').click(searchSubmit);
   $('#link-search').click(searchNav);
   $('#link-connections').click(connectionsNav);
   $('#button-newConnection').click(newConnection);
   tbodyHtml.connections = $('#tbody-connections').html();
}

function loginSubmit() {
   $('.page-all').hide();
   $('#page-search').show();
   var username = $('#input-username').val();
   console.log('login', username);
}

function searchSubmit() {
   var searchVal = $('#input-search').val();
   console.log('search', searchVal);
}

function searchNav() {
   console.log('linkSearch');
   $('.link-all').removeClass('active');
   $('#link-search').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-search').show();
}

function connectionsNav() {
   console.log('linkConnections');
   $('.link-all').removeClass('active');
   $('#link-connections').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-connections').show();
   $('#tbody-connections').empty();
   server.post({
      url: '/connection/list',
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
}

function deleteConnection(connection) {
   console.log('deleteConnection', connection);
   server.post({
      url: '/connection/delete/' + connection.connectionName,
      data: connection.connectionName,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {
            
         }
      }
   });   
}

function editConnection(connection) {
   console.log('editConnection', connection);
   server.post({
      url: '/connection/update/' + connection.connectionName,
      data: connection,
      success: function(res) {
         console.log(res);
         if (res.error) {
         } else {            
         }
      }
   });   
   
}
