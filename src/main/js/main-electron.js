//This file is basically the standard one to wrap up an appplication with electron.

const electron = require('electron');
// Module to control application life.
const {app} = electron;
// Module to create native browser window.
const {BrowserWindow} = electron;

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let win;

function createWindow() {
    // Create the browser window.
    win = new BrowserWindow({width: 800, height: 600, 'node-integration': false, title: 'SXP network', frame: true});

    // and load the index.html of the app.
    win.loadURL(`file://${__dirname}/html/index.html`);

    //Open dev tools
    //win.openDevTools();

    // Emitted when the window is closed.
    win.on('closed', () = > {
        // Dereference the window object, usually you would store windows
        // in an array if your app supports multi windows, this is the time
        // when you should delete the corresponding element.
        win = null;
})
    ;
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow);

// Quit when all windows are closed.
app.on('window-all-closed', () = > {
    // On OS X it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if(process.platform !== 'darwin'
)
{
    app.quit();
}
})
;

app.on('activate', () = > {
    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if(win === null
)
{
    createWindow();
}
})
;

app.on('certificate-error', (event, webContents, url, error, certificate, callback) = > {
    var nodeConsole = require('console');
var myConsole = new nodeConsole.Console(process.stdout, process.stderr);
myConsole.log('Certificate-error caught.');
if (url.startsWith('https://localhost')) {
    myConsole.log("Certificate-error management.")
    // Verification logic.
    event.preventDefault()
    callback(true)
} else {
    callback(false)
}
})
;


// In this file you can include the rest of your app's specific main process
// code. You can also put them in separate files and require them here.
