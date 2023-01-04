$(function() {
   $(':submit').click(function() {
      let link = ($('#link').val());
      let URL = link;
      parser(URL);
   })

});


function parser(URL) {
   $.ajax({
       url: '/parser',
       type: 'post',
       data: {
           url: URL
       },
       success: function (data) {
           alert(data.toString());
       }
   });
}

