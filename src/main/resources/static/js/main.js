$(function() {
   $(':submit').click(function() {
      let link = ($('#link').val());
      alert(link);
      let URL = link;
      parser(URL);
   })

});


function parser(URL) {
   let token = $("meta[name='_csrf']").attr("content");
   let header = $("meta[name='_csrf_header']").attr("content");
   $.ajax({
       url: '/parse',
       type: 'post',
       data: {
           url: URL
       },
       beforeSend: function (xhr) {
           xhr.setRequestHeader(header, token);
       },
       success: function (data) {
           elem.find('.types-panel__types-icon').css({'background-image':'url("' + data + '")'});
       }
   });
}

