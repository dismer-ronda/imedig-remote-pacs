var es_pryades_fullscreen_FullScreenExtension = function() {

    var state = this.getState();

    var checkFullScreen = function(state) {
        if (state.fullscreen) {
            screenfull.request();
        } else {
            screenfull.exit();
        }
    };

    checkFullScreen(state);

    this.onStateChange = function() {
        // checkFullScreen(state);
        document.getElementsByClassName(state.triggerClass)[0].addEventListener('click', function() {
            if (screenfull.enabled) {
                if (screenfull.isFullscreen) {
                    screenfull.exit();
                } else {
                    screenfull.request();
                }
            }
        });
        var _this = this;
        screenfull.on('change', function() {
            _this.changeFullScreen(screenfull.isFullscreen);
        });
    };
};
