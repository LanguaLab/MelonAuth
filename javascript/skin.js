
window.onload = function(){
var url = new URL(window.location.href);
var skin = url.searchParams.get('skin');
if(skin==null) {
    return;
}
var skinFile = this.document.createElement("a");
var link = "data:image/png;base64,"+ skin;
skinFile.href = link;
this.console.log(link);

skinFile.download = "skin.png";
skinFile.click();
}

            
