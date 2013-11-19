
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
   pullConnections();
   $('form').submit(function(event) {
      event.preventDefault();
      return false;
   });
   $('form input').keyup(function(event) {
      return event.which !== 13;
   });
   $('#input-replace').keyup(function(event) {
      if (this.value) {
         $('#button-replace').removeAttr('disabled');
      } else {
         $('#button-replace').attr('disabled', 'disabled');
      }
   });
   $('.page-all').hide();
   $('.page-all').removeClass('hide');
   $('#page-login').show();
   $('#input-username').focus();
   $('#connection-cancel').click(cancelConnectionForm);
   $('#connection-save').click(saveConnectionForm);
   $('#button-newConnection').click(newConnection);
   $('form.search').find('button[name=search]').click(search);
   $('#nav-search').click(navigateSearch);
   $('#nav-connections').click(navigateConnections);
   $('#nav-results').click(navigateResults);
   $('#button-replace').click(replace);
   $('#checkbox-results').click(function() {      
      var checked = $('#checkbox-results').is(':checked');
      console.log('checkbox', checked);      
      $.each(state.results, function(index, object) {         
         object.checked = checked;
      });
      renderResults(state.results);
   });
   tbodyHtml.connections = $('#tbody-connections').html();
   tbodyHtml.results = $('#tbody-results').html();
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
   $('#message-search').text('');
   console.log('login', username);
   navigateSearch();
}

function search() {
   var data = $('form.search').serialize();
   state.connectionName = $('form.search').find('select[name=connectionName]').val();
   state.searchString = $('form.search').find('input[name=searchString]').val();
   console.log('search', data);
   $('#message-search').text('');
   $.ajax({
      type: 'POST',
      url: '/app/search',
      data: data,
      success: function(results) {
         console.log(results);
         if (results.error) {
            $('#message-search').text('No results');
         } else {
            state.results = results;
            if (results.length > 0) {
               $('#message-results').text(results.length + 
                       ' results for \'' + state.searchString + '\'');  
               $('#message-search').text(results.length + ' results');
               renderResults(results);
            } else {
               $('#message-search').text('No results');
            }
         }
      },
      error: function() {
         console.log('error');
         $('#message-search').text('No results');
      }
   });
}

function renderResults(results) {
   console.log('renderResults', results.length);
   $('li.nav-all').removeClass('active');
   $('#nav-results').parent('li').addClass('active');
   $('#checkbox-results').prop('checked', false);
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
      tr.find('span.td-content').text(results[i].content.substring(0, 48));
      tr.find('button.button-view').click(results[i], function(event) {
         console.log('edit', event.data);
         showResult(event.data);
         return false;
      });
      var checkbox = tr.find('input[name=check]').first();
      checkbox.prop('checked', results[i].checked === true);
      checkbox.click(results[i], function(event) {
         console.log("checked", event.target.checked);
         event.data.checked = event.target.checked;
      });
      checkbox.closest('td').click(results[i], function(event) {
         checkbox.checked = true;
      });
      tr.click(results[i], function(event) {
         if (event.target.name !== 'check') {
            showResult(event.data);
         }
      });
   }
   $('.page-all').hide();
   $('#page-results').show();
}

function showResult(result) {
   console.log('showResult', result);
   $('li.nav-all').removeClass('active');
   $('#nav-result').parent('li').addClass('active');
   $('#result-connectionName').text(result.connectionName);
   $('#result-tableName').text(result.tableName);
   $('#result-columnName').text(result.columnName);
   $('#result-rowId').text(result.rowId);
   $('#result-content').text(result.content);
   $('.page-all').hide();
   $('#page-result').show();
}

function populateConnections() {
   var options = $('form.search').find('select[name=connectionName]');
   options.empty();
   $.each(state.connections, function() {
      options.append($("<option />").val(this.connectionName).text(this.connectionName));
   });
}

function navigateSearch() {
   $('#message-search').text('');
   $('#input-replace').val();
   console.log('linkSearch');
   enableSearch();
   $('form.search').find('input[name=searchString]').keyup(function() {
      enableSearch();
      return event.which !== 13;
   });
   $('form.search').find('input[name=searchString]').focus(function() {
      enableSearch();
   });
   $('.nav-all').removeClass('active');
   $('#nav-search').parent('li').addClass('active');
   $('.page-all').hide();
   $('#page-search').show();
   $('form.search').find('input[name=searchString]').focus();
   return false;
}

function enableSearch() {
   $('#message-search').text('');
   if ($('form.search').find('input[name=searchString]').val()) {
      $('form.search').find('button[name=search]').removeAttr('disabled');
   } else {
      $('form.search').find('button[name=search]').attr('disabled', 'disabled');
   }
}

function navigateResults(event) {
   console.log('navigateResults');
   if (state.results) {
      $('#message-results').text('');     
      renderResults(state.results);
   }
   return false;
}

function replace(event) {
   state.replaceString = $('#input-replace').val();
   console.log('replace', state.replaceString);
   state.matches = [];
   $.each(state.results, function(index, value) {
      console.log('each', index, value);
      if (value.checked) {
         state.matches.push(value);
      }
   });
   if (state.matches.length > 0) {
      $.ajax({
         type: 'POST',
         url: '/app/replace',
         dataType: 'json',
         contentType: "application/json",
         data: JSON.stringify({
            connectionName: state.connectionName,
            searchString: state.searchString,
            replaceString: state.replaceString,
            matches: state.matches
         }),
         success: function(results) {
            console.log(results);
            if (results.error) {
               $('#message-results').text('Error');
            } else {
               state.results = results;
               if (results.length === 1) {
                  $('#message-results').text('Replaced in single record');
                  renderResults(results);
               } else if (results.length > 0) {
                  $('#message-results').text('Replaced in ' + results.length + ' records');
                  renderResults(results);
               } else {
                  $('#message-results').text('No replacements');
               }
            }
         },
         error: function() {
            console.log('error');
            $('#message-search').text('No results');
         }
      });
   }
}
