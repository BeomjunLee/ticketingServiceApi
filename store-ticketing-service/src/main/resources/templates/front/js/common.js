//메뉴(펼침)
$(document).ready(function(){
	$('.menu02 li').hover(function(){
		$('ul', this).slideDown(400);
		$(this).children('a:first').addClass("hov");
	}, function(){
		$('ul', this).slideUp(200);
		$(this).children('a:first').removeClass("hov");
	});
});

//joinStoreAdmin.html //주소
//"검색" 단추를 누르면 팝업 레이어가 열리도록 설정한다
/*$(function() { $("#postcodify_search_button").postcodifyPopUp(); });
$("#search_button").postcodifyPopUp();*/



