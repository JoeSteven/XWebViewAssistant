(function() {
    var responseCallbacks = {};
    var uniqueId = 1;


    // invoke java method
    function invokeJava(javaFunc, paramsJSON, callback) {
        var callbackId = '-1';
        if (callback) {
            callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = callback;

        }
        var json = {"callback_id":callbackId,"func":javaFunc, "params":paramsJSON}

        window.javaObject.invokeJavaMethod(window.btoa(JSON.stringify(json)))
    }

    // callback from java
    function _xwebview_callback(msg) {
        var json = JSON.parse(window.atob(msg));
        if (!json.callback_id || json.callback_id === "-1") {
            return
        }
        var id = json.callback_id;
        var responseCallback = responseCallbacks[json.callback_id];
        if (responseCallback){
            responseCallback(json)
            delete responseCallbacks[json.callback_id]
        }
    }

    window._xwebview_callback = _xwebview_callback
    window.invokeJavaFunc = invokeJava
})();