/**
 * Created by sjm on 2017/6/8.
 */
$("#register").click(function () {
    if ($("#passwd1").val() == $("#passwd2").val() && $("#passwd1").val() != "") {
        $.ajax({
            url: '/NetworkSimulation/registerUser',
            data: {
                username : $("#username"),
                psw : $("#passwd1")
            },
            type: 'post',
            dataType: 'json',
            async: false,
            success: function (msg) {
                $.alert(msg);
                if (msg == "success") {
                    window.location.reload();
                    opener.location.reload();
                }
            },
            error: function () {

            }
        });
    } else {
        $.alert("两次密码输入有误，请重新输入！");
        $("#passwd1").val("");
        $("#passwd2").val("");
    }
});

