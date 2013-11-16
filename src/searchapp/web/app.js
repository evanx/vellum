

function documentReady() {
    console.log('documentReady');
    $('.page-all').hide();
    $('.page-all').removeClass('hide');
    $('#page-login').show();
    $('#input-username').focus();
    $('#button-login').click(login);
    $('#button-search').click(search);    
}

function login() {
    $('.page-all').hide();
    $('#page-search').show();
}

function search() {
    var searchVal = $('#input-search').val();
    console.log('search', searchVal);
}