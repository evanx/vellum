
function navigateConnections(event) {
   console.log('navigateConnections');
   $('.nav-all').removeClass('active');
   $('#nav-connections').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-connections').show();
   pullConnections();
   return false;
}

function pullConnections() {
   $.ajax({
      type: 'POST',
      data: '',
      url: '/app/connection/list',
      success: function(res) {
         console.log("list", res.length);
         if (res.error) {
         } else {
            state.connections = res;
            populateConnections();
            renderConnections();
            $('#button-login').click(login);
         }
      }
   });
}

function renderConnections() {
   $('#tbody-connections').empty();
   for (var i = 0; i < state.connections.length; i++) {
      console.log("row", i, state.connections[i]);
      $('#tbody-connections').append(tbodyHtml.connections);
      var tr = $("#tbody-connections > tr:last-child");
      tr.find('span.td-name').text(state.connections[i].connectionName);
      tr.find('span.td-driver').text(state.connections[i].driver);
      tr.find('span.td-url').text(state.connections[i].url);
      tr.find('span.td-user').text(state.connections[i].user);
      tr.find('button.button-edit').click(state.connections[i], function(event) {
         console.log('edit', event.data);
         editConnection(event.data);
         return false;
      });
      tr.find('button.button-remove').click(state.connections[i], function(event) {
         console.log('remove', event.data);
         $(this).closest('tr').remove();
         deleteConnection(event.data);
         return false;
      });
      tr.click(state.connections[i], function(event) {
         editConnection(event.data);
      });
   }
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
   $('form.connection').submit(function(event) {
      event.preventDefault();
      return false;  
   });   
   
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
   navigateConnections();
   return false;
}

function saveConnectionForm(event) {
   $('form.connection').find('input[name=connectionName]').removeAttr('disabled');
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
            navigateConnections();
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
            navigateConnections();
         }
      },
      error: function() {
         console.log('error');
      }
   });
}
