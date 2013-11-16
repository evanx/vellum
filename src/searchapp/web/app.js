

function documentReady() {
    console.log('documentReady');
    $('.page-all').hide();
    $('.page-all').removeClass('hide');
    $('#page-login').show();
    $('#input-username').focus();
    $('#button-login').click(buttonLogin);
    $('#button-search').click(buttonSearch);
    $('#link-search').click(linkSearch);
    $('#link-connections').click(linkConnections);
}

function buttonLogin() {
    $('.page-all').hide();
    $('#page-search').show();
    var username = $('#input-username').val();
    console.log('login', username);
}

function buttonSearch() {
    var searchVal = $('#input-search').val();
    console.log('search', searchVal);
}

function linkSearch() {
    console.log('linkSearch');
   $('.link-all').removeClass('active');
   $('#link-search').parent('li').addClass('active');
 }

function linkConnections() {
    console.log('linkConnections');
   $('.link-all').removeClass('active');
   $('#link-connections').parent('li').addClass('active');
}

