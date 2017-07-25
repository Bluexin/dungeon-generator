$(document).ready(function () {
    var main = $("#main-content");
    var pub = main.find("> div:first");
    console.log("Found " + pub);
    pub.hide();
    var snd = main.find("> div:nth-child(2)");
    snd.hide();
});
