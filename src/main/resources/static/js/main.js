$(document).ready(function() {
   $(':submit').click(function() {
      let URL = ($('#link').val());
      parser(URL);
   })

function parser(URL) {
    $('#preloader').removeAttr('hidden');
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
            $('#preloader').hide(1000);
            alert('done')
            }
        });
    }

});

$(".btn-loadandsavegoods").click(function () {
    console.log('load goods');
    $('#preloader').removeAttr('hidden');
    $.get("/goods", {}).done(function (data) {
        $('#preloader').hide(1000);
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


// $('.btn-usersinxls').click(function () {
//     $.when(ajaxGet('/getUsersList'), $('#lex_preloader').removeAttr('hidden'))
//         .done(function (data) {
//             $('#lex_preloader').hide(1000);
//             window.location = '/downloadgoodswithuncorrectedurlfile/' + data;
//             swal({
//                 title: "Файл сформирован",
//                 type: 'success'
//             });
//         });
// });

