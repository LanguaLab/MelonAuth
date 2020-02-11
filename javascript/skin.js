window.onload = function () {
    const url = new URL(window.location.href);
    const file = url.searchParams.get('file');
    const skin = url.searchParams.get('skin');
    if (skin == null) {
        return;
    }
    const skinFile = this.document.createElement("a");
    const link = "data:image/png;base64," + skin;
    skinFile.href = link;
    skinFile.download = file+".png";
    skinFile.click();
};

            
