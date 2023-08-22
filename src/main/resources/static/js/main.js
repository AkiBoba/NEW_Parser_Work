$(document).ready(function() {
   $(':submit').click(function() {
      let URL = ($('#link').val());
      parser(URL);
   })

function parser(URL) {
    $.ajax({
        url: '/parser',
        type: 'post',
        data: {
        url: URL
        },
        success: function (data) {
            // data.forEach(link => {
            //     let attr = `<p style="text-align:left; margin-left:25%;"><a href="#">автомобиль</a></p>`;
            //     $('.form1').append(attr);
            //     $('#href').attr("href", link);
            //     })
            alert('done')
            }
        });
    }

});

$(".btn-loadandsavegoods").click(function () {
    console.log('load goods');
    $.get("/goods", {}).done(function (data) {
        if (data) {
            window.location = '/downloadorderslfile/' + data;
            swal({
                title: "Файл загружен",
                type: 'success'
            });
        } else {
            alert('Что то пошло не так');
        }
    });
});

