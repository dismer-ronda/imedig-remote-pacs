(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["FabricJs"] = factory();
	else
		root["FabricJs"] = factory();
})(this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.l = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };

/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};

/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};

/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 9);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Utils = exports.Utils = function () {
    function Utils() {
        _classCallCheck(this, Utils);
    }

    _createClass(Utils, null, [{
        key: 'createLine',
        value: function createLine(points, configuration) {
            var line = new fabric.Line(points, {
                strokeWidth: configuration.strokeWidth,
                fill: configuration.fillColor,
                stroke: configuration.strokeColor,
                strokeDashArray: configuration.strokeDashArray,
                strokeLineCap: configuration.strokeLineCap,
                visible: configuration.visible,
                originX: 'center',
                originY: 'center',
                shadow: configuration.figureShadow,
                objectCaching: false
            });
            return line;
        }
    }, {
        key: 'createRulerLine',
        value: function createRulerLine(points, configuration) {
            var line = new fabric.Line(points, {
                strokeWidth: configuration.strokeWidth,
                fill: configuration.fillColor,
                stroke: configuration.strokeColor,
                strokeDashArray: configuration.strokeDashArray,
                strokeLineCap: configuration.strokeLineCap,
                visible: configuration.visible,
                shadow: configuration.figureShadow,
                objectCaching: false
            });
            return line;
        }
    }, {
        key: 'createRect',
        value: function createRect(points, configuration) {
            var rect = new fabric.Rect({
                left: points[0],
                top: points[1],
                width: points[2],
                height: points[3],
                strokeDashArray: configuration.strokeDashArray,
                strokeLineCap: configuration.strokeLineCap,
                visible: configuration.visible,
                fill: 'transparent',
                stroke: configuration.strokeColor,
                strokeWidth: configuration.strokeWidth,
                shadow: configuration.figureShadow,
                objectCaching: false
            });
            return rect;
        }
    }, {
        key: 'createTextLabel',
        value: function createTextLabel(text, options, configuration) {
            var textObject = new fabric.Text(text, {
                backgroundColor: configuration.textBackgroundColor,
                fill: configuration.textFillColor,
                fontWeight: configuration.textFontWeight,
                fontStyle: configuration.textFontStyle,
                fontSize: configuration.textFontSize,
                fontFamily: configuration.textFontFamily,
                left: options.left,
                top: options.top,
                visible: configuration.visible,
                selectable: false,
                shadow: configuration.textShadow //,
                // objectCaching: false
            });
            return textObject;
        }
    }, {
        key: 'createTextNotes',
        value: function createTextNotes(text, configuration) {
            var textObject = new fabric.Text(text, {
                backgroundColor: configuration.textBackgroundColor,
                fill: configuration.textFillColor,
                fontWeight: configuration.textFontWeight,
                fontStyle: configuration.textFontStyle,
                fontSize: configuration.textFontSize,
                fontFamily: configuration.textFontFamily,
                left: configuration.textLeft,
                top: configuration.textTop,
                textAlign: configuration.textAlign,
                shadow: configuration.textShadow //,
                //objectCaching: false
            });
            return textObject;
        }
    }, {
        key: 'generateUniqueId',
        value: function generateUniqueId() {
            return 'obj_' + Math.random().toString(36).substr(2, 16) + "_" + new Date().getTime();
        }
    }, {
        key: 'calculateOriginX',
        value: function calculateOriginX(canvas, text, originX, originY) {

            switch (originX) {
                case "left":
                    text.set({
                        left: 0
                    });
                    break;
                case "center":
                    switch (originY) {
                        case "top":
                            text.centerH();
                            text.set({
                                top: 0
                            });
                            break;
                        case "bottom":
                            text.centerH();
                            var bottom = canvas.getHeight() - text.getScaledHeight();
                            text.set({
                                top: bottom
                            });
                            break;
                        default:
                            text.center();
                            break;
                    }

                    break;
                case "right":
                    var right = canvas.getWidth() - text.getScaledWidth();
                    text.set({
                        left: right
                    });
                    break;
            }
        }
    }, {
        key: 'calculateOriginY',
        value: function calculateOriginY(canvas, text, originX, originY) {
            switch (originY) {
                case "top":
                    text.set({
                        top: 0
                    });
                    break;
                case "center":
                    text.center();
                    break;
                case "bottom":
                    var bottom = canvas.getHeight() - text.getScaledHeight();
                    text.set({
                        top: bottom
                    });
                    break;
            }
        }
    }]);

    return Utils;
}();

/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Figure = exports.Figure = function Figure(key, figureType, points, text, Drawed) {
    _classCallCheck(this, Figure);

    this.key = key;
    this.figureType = figureType;
    this.points = points;
    this.text = "";
    this.Drawed = Drawed;
};

/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.AngleDrawer = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _figure = __webpack_require__(1);

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var AngleDrawer = exports.AngleDrawer = function () {
    function AngleDrawer(options) {
        _classCallCheck(this, AngleDrawer);

        this.configuration = options.configuration;
        this.component = options.component;
        this.isFirtLine = true;
        this.points = [];
        this.movePointer = null;
        this.drawMode = options.drawMode;
        this.preventUp = false;
        this.offCanvas = false;
        this.parent = options.parent;
    }

    _createClass(AngleDrawer, [{
        key: 'setDrawMode',
        value: function setDrawMode(drawMode) {
            this.drawMode = drawMode;
        }
    }, {
        key: 'setConfiguration',
        value: function setConfiguration(configuration) {
            this.configuration = configuration;
        }
    }, {
        key: 'onMouseMove',
        value: function onMouseMove(event, canvas) {
            if (this.currentDraw) {
                var pointer = canvas.getPointer(event.e);

                this.movePointer = pointer;

                this.currentDraw.set({
                    x2: pointer.x,
                    y2: pointer.y
                });
                canvas.renderAll();
            }
        }
    }, {
        key: 'onMouseUp',
        value: function onMouseUp(event, canvas) {
            if (this.preventUp) {
                return;
            }

            var pointer = canvas.getPointer(event.e);

            if (this.isFirtLine) {
                if (pointer.eq(this.points[0])) {
                    canvas.remove(this.currentDraw);
                    this.firstLine = true;
                    return;
                }
            }

            if (pointer.x < 0 || pointer.y < 0 || pointer.x > this.rect.br.x || pointer.y > this.rect.br.y) {
                this.points.push(this.movePointer);
            } else {
                this.points.push(pointer);
            }

            if (!this.isFirtLine) {

                if (this.firstLine) {
                    canvas.remove(this.firstLine);
                }
                if (this.secondLine) {
                    canvas.remove(this.secondLine);
                }

                this.figure = new _figure.Figure(_utils.Utils.generateUniqueId(), "ANGLE", this.points, "", this.drawMode === "DRAW");

                if (this.drawMode === "SHOW") {
                    canvas.remove(this.currentDraw);
                    canvas.renderAll();
                } else {
                    this.angle = new fabric.Group([this.firstLine, this.secondLine], {
                        perPixelTargetFind: true
                    });
                    canvas.add(this.angle);
                    this.parent.setFigure(this.figure.key, this.angle);
                }

                this.component.onDrawFigure(JSON.stringify(this.figure));
            }
            this.isFirtLine = !this.isFirtLine;
        }
    }, {
        key: 'onMouseDown',
        value: function onMouseDown(event, canvas) {
            this.preventUp = false;
            var pointer = canvas.getPointer(event.e);
            this.rect = canvas.calcViewportBoundaries();

            if (this.isFirtLine) {
                this.points = [];
                this.points.push(pointer);
                this.drawLine(pointer, canvas);
                this.firstLine = this.currentDraw;
            } else {
                this.drawLine(this.points[1], canvas);
                this.currentDraw.set({
                    x2: pointer.x,
                    y2: pointer.y
                });
                this.secondLine = this.currentDraw;
            }
            canvas.renderAll();
        }
    }, {
        key: 'drawLine',
        value: function drawLine(pointer, canvas) {
            var pointsToDraw = [pointer.x, pointer.y, pointer.x, pointer.y];
            this.currentDraw = _utils.Utils.createLine(pointsToDraw, this.configuration);
            canvas.add(this.currentDraw);
        }
    }, {
        key: 'setText',
        value: function setText(text, canvas) {

            if (this.drawMode === "SHOW") {
                return;
            }

            if (this.figure) {
                this.figure.text = text;
            }

            if (this.angle) {
                canvas.remove(this.angle);
            }

            if (this.lineText) {
                canvas.remove(this.lineText);
            }

            var x1 = this.points[1].x;
            var x2 = this.points[2].x;
            var y1 = this.points[1].y;
            var y2 = this.points[2].y;

            var deltaY = y2 - y1;
            var deltaX = x2 - x1;

            //const pad = 1 / 2;

            //const angleInDegrees = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            this.lineText = _utils.Utils.createTextLabel(text, {
                left: left,
                top: top
            }, this.configuration);
            var group = new fabric.Group([this.angle, this.lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
            this.parent.setFigure(this.figure.key, group);
        }
    }, {
        key: 'cancelDraw',
        value: function cancelDraw(canvas) {
            this.preventUp = true;
            this.isFirtLine = true;
            this.points = [];
            this.movePointer = null;

            if (this.firstLine) {
                canvas.remove(this.firstLine);
            }

            if (this.secondLine) {
                canvas.remove(this.secondLine);
            }

            if (this.angle) {
                canvas.remove(this.angle);
            }
        }
    }]);

    return AngleDrawer;
}();

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.Drawer = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Drawer = exports.Drawer = function () {
    function Drawer() {
        _classCallCheck(this, Drawer);
    }

    _createClass(Drawer, null, [{
        key: 'drawLine',
        value: function drawLine(line, canvas, configuration) {

            var x1 = line.points[0].x;
            var x2 = line.points[1].x;
            var y1 = line.points[0].y;
            var y2 = line.points[1].y;

            var points = [x1, y1, x2, y2];
            var lineDrawer = _utils.Utils.createLine(points, configuration);
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;

            var lineText = _utils.Utils.createTextLabel(line.text, {
                left: left,
                top: top
            }, configuration);

            var group = new fabric.Group([lineDrawer, lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
        }
    }, {
        key: 'drawAngle',
        value: function drawAngle(angle, canvas, configuration) {

            var x1 = angle.points[1].x;
            var x2 = angle.points[2].x;
            var y1 = angle.points[1].y;
            var y2 = angle.points[2].y;

            var firstLinePoints = [angle.points[0].x, angle.points[0].y, angle.points[1].x, angle.points[1].y];
            var secondLinePoints = [angle.points[1].x, angle.points[1].y, angle.points[2].x, angle.points[2].y];

            var firstLineDrawer = _utils.Utils.createLine(firstLinePoints, configuration);

            var secondLineDrawer = _utils.Utils.createLine(secondLinePoints, configuration);
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            var lineText = _utils.Utils.createTextLabel(angle.text, {
                left: left,
                top: top
            }, configuration);

            var group = new fabric.Group([firstLineDrawer, secondLineDrawer, lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
        }
    }, {
        key: 'drawFreeAngle',
        value: function drawFreeAngle(angle, canvas, configuration) {

            var x1 = angle.points[2].x;
            var x2 = angle.points[3].x;
            var y1 = angle.points[2].y;
            var y2 = angle.points[3].y;

            var firstLinePoints = [angle.points[0].x, angle.points[0].y, angle.points[1].x, angle.points[1].y];
            var secondLinePoints = [angle.points[2].x, angle.points[2].y, angle.points[3].x, angle.points[3].y];

            var firstLineDrawer = _utils.Utils.createLine(firstLinePoints, configuration);

            var secondLineDrawer = _utils.Utils.createLine(secondLinePoints, configuration);
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            var lineText = _utils.Utils.createTextLabel(angle.text, {
                left: left,
                top: top
            }, configuration);

            var group = new fabric.Group([firstLineDrawer, secondLineDrawer, lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
        }
    }, {
        key: 'drawRect',
        value: function drawRect(rect, canvas, configuration) {

            var x1 = angle.points[0].x;
            var x2 = angle.points[1].x;
            var y1 = angle.points[0].y;
            var y2 = angle.points[1].y;

            var rectCoords = [rect.points[0].x, rect.points[0].y, rect.points[1].x - rect.points[0].x, rect.points[1].y - rect.points[0].y];

            var rectDrawer = _utils.Utils.createRect(rectCoords, configuration);

            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            var lineText = _utils.Utils.createTextLabel(angle.text, {
                left: left,
                top: top
            }, configuration);

            var group = new fabric.Group([rectDrawer, lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
        }
    }]);

    return Drawer;
}();

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.FreeAngleDrawer = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _figure = __webpack_require__(1);

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var FreeAngleDrawer = exports.FreeAngleDrawer = function () {
    function FreeAngleDrawer(options) {
        _classCallCheck(this, FreeAngleDrawer);

        this.configuration = options.configuration;
        this.component = options.component;
        this.isFirtLine = true;
        this.points = [];
        this.movePointer = null;
        this.drawMode = options.drawMode;
        this.preventUp = false;
        this.parent = options.parent;
    }

    _createClass(FreeAngleDrawer, [{
        key: 'setDrawMode',
        value: function setDrawMode(drawMode) {
            this.drawMode = drawMode;
        }
    }, {
        key: 'setConfiguration',
        value: function setConfiguration(configuration) {
            this.configuration = configuration;
        }
    }, {
        key: 'onMouseMove',
        value: function onMouseMove(event, canvas) {
            if (this.currentDraw) {
                var pointer = canvas.getPointer(event.e);

                if (pointer.x < 0 || pointer.y < 0 || pointer.x > this.rect.br.x || pointer.y > this.rect.br.y) {
                    return;
                }

                this.movePointer = pointer;

                this.currentDraw.set({
                    x2: pointer.x,
                    y2: pointer.y
                });
                canvas.renderAll();
            }
        }
    }, {
        key: 'onMouseUp',
        value: function onMouseUp(event, canvas) {
            if (this.preventUp) {
                return;
            }

            var pointer = canvas.getPointer(event.e);

            if (this.isFirtLine) {
                if (pointer.eq(this.points[0])) {
                    canvas.remove(this.currentDraw);
                    this.firstLine = true;
                    return;
                }
            }

            if (pointer.x < 0 || pointer.y < 0 || pointer.x > this.rect.br.x || pointer.y > this.rect.br.y) {
                this.points.push(this.movePointer);
            } else {
                this.points.push(pointer);
            }

            if (!this.isFirtLine) {

                if (this.firstLine) {
                    canvas.remove(this.firstLine);
                }
                if (this.secondLine) {
                    canvas.remove(this.secondLine);
                }

                this.figure = new _figure.Figure(_utils.Utils.generateUniqueId(), "FREE_ANGLE", this.points, "", this.drawMode === "DRAW");

                if (this.drawMode === "SHOW") {
                    canvas.remove(this.currentDraw);
                    canvas.renderAll();
                } else {
                    this.angle = new fabric.Group([this.firstLine, this.secondLine], {
                        selectable: false,
                        objectCaching: false
                    });
                    canvas.add(this.angle);
                    this.parent.setFigure(this.figure.key, this.angle);
                }

                this.component.onDrawFigure(JSON.stringify(this.figure));
            }
            this.isFirtLine = !this.isFirtLine;
        }
    }, {
        key: 'onMouseDown',
        value: function onMouseDown(event, canvas) {
            this.preventUp = false;
            var pointer = canvas.getPointer(event.e);
            this.rect = canvas.calcViewportBoundaries();

            if (this.isFirtLine) {
                this.points = [];
                this.points.push(pointer);
                this.drawLine(pointer, canvas);
                this.firstLine = this.currentDraw;
            } else {
                this.drawLine(pointer, canvas);
                this.points.push(pointer);
                this.currentDraw.set({
                    x2: pointer.x,
                    y2: pointer.y
                });
                this.secondLine = this.currentDraw;
            }
            canvas.renderAll();
        }
    }, {
        key: 'drawLine',
        value: function drawLine(pointer, canvas) {
            var pointsToDraw = [pointer.x, pointer.y, pointer.x, pointer.y];
            this.currentDraw = _utils.Utils.createLine(pointsToDraw, this.configuration);
            canvas.add(this.currentDraw);
        }
    }, {
        key: 'setText',
        value: function setText(text, canvas) {

            if (this.drawMode === "SHOW") {
                return;
            }

            if (this.figure) {
                this.figure.text = text;
            }

            if (this.angle) {
                canvas.remove(this.angle);
            }

            if (this.lineText) {
                canvas.remove(this.lineText);
            }

            var x1 = this.points[2].x;
            var x2 = this.points[3].x;
            var y1 = this.points[2].y;
            var y2 = this.points[3].y;

            var deltaY = y2 - y1;
            var deltaX = x2 - x1;

            //const pad = 1 / 2;

            //const angleInDegrees = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            this.lineText = _utils.Utils.createTextLabel(text, {
                left: left,
                top: top
            }, this.configuration);
            var group = new fabric.Group([this.angle, this.lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
            this.parent.setFigure(this.figure.key, group);
        }
    }, {
        key: 'cancelDraw',
        value: function cancelDraw(canvas) {
            this.preventUp = true;
            this.isFirtLine = true;
            this.points = [];
            this.movePointer = null;

            if (this.firstLine) {
                canvas.remove(this.firstLine);
            }

            if (this.secondLine) {
                canvas.remove(this.secondLine);
            }

            if (this.angle) {
                canvas.remove(this.angle);
            }
        }
    }]);

    return FreeAngleDrawer;
}();

/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.LineDrawer = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _figure = __webpack_require__(1);

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var LineDrawer = exports.LineDrawer = function () {
    function LineDrawer(options) {
        _classCallCheck(this, LineDrawer);

        this.configuration = options.configuration;
        this.component = options.component;
        this.points = [];
        this.drawMode = options.drawMode;
        this.preventUp = false;
        this.offCanvas = false;
        this.parent = options.parent;
    }

    _createClass(LineDrawer, [{
        key: 'setDrawMode',
        value: function setDrawMode(drawMode) {
            this.drawMode = drawMode;
        }
    }, {
        key: 'setConfiguration',
        value: function setConfiguration(configuration) {
            this.configuration = configuration;
        }
    }, {
        key: 'onMouseMove',
        value: function onMouseMove(event, canvas) {
            if (this.currentDraw) {
                var pointer = canvas.getPointer(event.e);

                this.currentDraw.set({
                    x2: pointer.x,
                    y2: pointer.y
                });
                canvas.renderAll();
            }
        }
    }, {
        key: 'onMouseUp',
        value: function onMouseUp(event, canvas) {
            if (this.preventUp) {
                return;
            }

            var pointer = canvas.getPointer(event.e);

            if (pointer.eq(this.points[0])) {
                canvas.remove(this.currentDraw);
                return;
            }

            this.points.push(pointer);
            this.figure = new _figure.Figure(_utils.Utils.generateUniqueId(), "LINE", this.points, "", this.drawMode === "DRAW");

            if (this.drawMode === "SHOW") {
                canvas.remove(this.currentDraw);
                canvas.renderAll();
            } else {
                this.parent.setFigure(this.figure.key, this.currentDraw);
            }

            this.component.onDrawFigure(JSON.stringify(this.figure));
        }
    }, {
        key: 'onMouseDown',
        value: function onMouseDown(event, canvas) {
            this.points = [];
            this.preventUp = false;
            var pointer = canvas.getPointer(event.e);
            this.points.push(pointer);
            var pointsToDraw = [pointer.x, pointer.y, pointer.x, pointer.y];
            this.currentDraw = _utils.Utils.createLine(pointsToDraw, this.configuration);

            canvas.add(this.currentDraw);
            canvas.renderAll();
        }
    }, {
        key: 'setText',
        value: function setText(text, canvas) {

            if (this.drawMode === "SHOW") {
                return;
            }

            if (this.figure) {
                this.figure.text = text;
            }

            if (this.currentDraw) {
                canvas.remove(this.currentDraw);
            }

            if (this.lineText) {
                canvas.remove(this.lineText);
            }

            var x1 = this.points[0].x;
            var x2 = this.points[1].x;
            var y1 = this.points[0].y;
            var y2 = this.points[1].y;

            var deltaY = y2 - y1;
            var deltaX = x2 - x1;

            //const pad = 1 / 2;

            //const angleInDegrees = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            this.lineText = _utils.Utils.createTextLabel(text, {
                left: left,
                top: top
            }, this.configuration);

            var group = new fabric.Group([this.currentDraw, this.lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
            this.parent.setFigure(this.figure.key, group);
        }
    }, {
        key: 'cancelDraw',
        value: function cancelDraw(canvas) {
            this.preventUp = true;
            this.points = [];
            if (this.currentDraw) {
                canvas.remove(this.currentDraw);
            }
        }
    }]);

    return LineDrawer;
}();

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.Loader = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Loader = exports.Loader = function () {
    function Loader(params) {
        _classCallCheck(this, Loader);

        this.canvas = params.canvas;
        this.loaderConfiguration = params.loaderConfiguration;
        if (this.loaderConfiguration.loaderText) {
            this.generateLoader(this.loaderConfiguration);
        }
    }

    _createClass(Loader, [{
        key: 'generateLoader',
        value: function generateLoader(loaderConfiguration) {
            var text = new fabric.Text(loaderConfiguration.loaderText, {
                backgroundColor: loaderConfiguration.textBackgroundColor,
                fill: loaderConfiguration.textFillColor,
                fontSize: loaderConfiguration.textFontSize,
                fontFamily: loaderConfiguration.textFontFamily,
                objectCaching: false
            });

            var rect = new fabric.Rect({
                width: text.getScaledWidth() + 40,
                height: loaderConfiguration.height,
                stroke: loaderConfiguration.strokeColor,
                strokeWidth: loaderConfiguration.strokeWidth,
                fill: loaderConfiguration.fillColor,
                selectable: false
            });

            if (loaderConfiguration.shadow) {
                rect.set("shadow", loaderConfiguration.shadow);
            }

            this.circle = new fabric.Circle({
                radius: loaderConfiguration.spinnerRadio,
                left: 15,
                angle: 0,
                startAngle: 0,
                endAngle: Math.PI,
                stroke: loaderConfiguration.spinnerColor,
                strokeWidth: loaderConfiguration.spinnerWidth,
                originX: 'center',
                originY: 'center',
                fill: 'transparent'
            });

            switch (loaderConfiguration.spinnerPosition) {
                case "LEFT":
                    this.circle.set("left", 2 * this.circle.get("radius"));

                    rect.set("height", this.circle.getScaledHeight() + text.getScaledHeight());
                    rect.set("width", text.getScaledWidth() + 2 * this.circle.get("radius") + this.circle.get("left") + 20);

                    this.circle.set("top", rect.getScaledHeight() / 2);

                    text.set("top", rect.getScaledHeight() / 2 - text.getScaledHeight() / 2);
                    text.set("left", 2 * this.circle.get("radius") + this.circle.get("left"));

                    break;
                case "TOP":
                    this.circle.set("top", this.circle.get("radius") + 10);

                    rect.set("height", 2 * this.circle.get("radius") + text.getScaledHeight() + this.circle.get("top"));

                    this.circle.set("left", rect.getScaledWidth() / 2);

                    text.set("top", 1.5 * this.circle.get("radius") + this.circle.get("top"));
                    text.set("left", rect.getScaledWidth() / 2 - text.getScaledWidth() / 2);
                    break;

                case "RIGHT":
                    text.set("left", 12);

                    this.circle.set("left", text.getScaledWidth() + 2 * text.get("left") + this.circle.get("radius"));

                    rect.set("height", this.circle.getScaledHeight() + text.getScaledHeight());
                    rect.set("width", text.getScaledWidth() + 2 * this.circle.get("radius") + text.get("left") + 20);

                    this.circle.set("top", rect.getScaledHeight() / 2);

                    text.set("top", rect.getScaledHeight() / 2 - text.getScaledHeight() / 2);
                    break;

                case "BOTTOM":
                    text.set("top", 10);
                    this.circle.set("top", text.getScaledHeight() + text.get("top") + 1.5 * this.circle.get("radius"));

                    rect.set("height", 2 * this.circle.get("radius") + text.getScaledHeight() + this.circle.get("top") / 2);

                    this.circle.set("left", rect.getScaledWidth() / 2);
                    text.set("left", rect.getScaledWidth() / 2 - text.getScaledWidth() / 2);
                    break;
            }

            var group = new fabric.Group([rect, this.circle, text], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });

            this.loader = group;
        }
    }, {
        key: 'showLoader',
        value: function showLoader() {
            if (this.loaderConfiguration.loaderText) {
                var r = 0;
                var _this = this;
                this.animateInterval = setInterval(function () {
                    r += Math.PI / 180 + 90;
                    if (r >= 999999) {
                        r = 0;
                    }

                    _this.circle.animate({
                        angle: r
                    }, {
                        duration: 1000,
                        onChange: _this.canvas.renderAll.bind(_this.canvas),
                        easing: fabric.util.ease.easeOutExpo
                    });
                }, 200);

                this.canvas.add(_this.loader);

                _utils.Utils.calculateOriginY(this.canvas, _this.loader, _this.loaderConfiguration.originX, _this.loaderConfiguration.originY);
                _utils.Utils.calculateOriginX(this.canvas, _this.loader, _this.loaderConfiguration.originX, _this.loaderConfiguration.originY);

                this.canvas.lowerCanvasEl.style.zIndex = 10;
            }
        }
    }, {
        key: 'hideLoader',
        value: function hideLoader() {
            if (this.loader) {
                this.canvas.lowerCanvasEl.style.zIndex = 2;
                clearInterval(this.animateInterval);
                this.canvas.remove(this.loader);

                this.circle.animate({
                    angle: 0
                }, {
                    duration: 1000,
                    onChange: this.canvas.renderAll.bind(this.canvas),
                    easing: fabric.util.ease.easeOutExpo
                });
            }
        }
    }]);

    return Loader;
}();

/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.RectDrawer = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _figure = __webpack_require__(1);

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var RectDrawer = exports.RectDrawer = function () {
    function RectDrawer(options) {
        _classCallCheck(this, RectDrawer);

        this.configuration = options.configuration;
        this.component = options.component;
        this.points = [];
        this.drawMode = options.drawMode;
        this.preventUp = false;
        this.parent = options.parent;
    }

    _createClass(RectDrawer, [{
        key: 'setDrawMode',
        value: function setDrawMode(drawMode) {
            this.drawMode = drawMode;
        }
    }, {
        key: 'setConfiguration',
        value: function setConfiguration(configuration) {
            this.configuration = configuration;
        }
    }, {
        key: 'onMouseMove',
        value: function onMouseMove(event, canvas) {
            if (this.currentDraw) {
                var pointer = canvas.getPointer(event.e);

                if (pointer.x < 0 || pointer.y < 0 || pointer.x > this.rect.br.x || pointer.y > this.rect.br.y) {
                    return;
                }

                if (this.points[0].x > pointer.x) {
                    this.currentDraw.set({
                        left: Math.abs(pointer.x)
                    });
                }
                if (this.points[0].y > pointer.y) {
                    this.currentDraw.set({
                        top: Math.abs(pointer.y)
                    });
                }

                this.currentDraw.set({
                    width: Math.abs(this.points[0].x - pointer.x)
                });
                this.currentDraw.set({
                    height: Math.abs(this.points[0].y - pointer.y)
                });

                canvas.renderAll();
            }
        }
    }, {
        key: 'onMouseUp',
        value: function onMouseUp(event, canvas) {
            if (this.preventUp) {
                return;
            }
            var pointer = canvas.getPointer(event.e);

            if (pointer.eq(this.points[0])) {
                canvas.remove(this.currentDraw);
                return;
            }

            this.points.push(pointer);
            this.figure = new _figure.Figure(_utils.Utils.generateUniqueId(), "RECT", this.points, "", this.drawMode === "DRAW");

            if (this.drawMode === "SHOW") {
                canvas.remove(this.currentDraw);
                canvas.renderAll();
            } else {
                this.parent.setFigure(this.figure.key, this.currentDraw);
            }

            this.component.onDrawFigure(JSON.stringify(this.figure));
        }
    }, {
        key: 'onMouseDown',
        value: function onMouseDown(event, canvas) {
            this.preventUp = false;
            this.points = [];
            var pointer = canvas.getPointer(event.e);
            this.rect = canvas.calcViewportBoundaries();
            this.points.push(pointer);
            var pointsToDraw = [pointer.x, pointer.y, 0, 0];
            this.currentDraw = _utils.Utils.createRect(pointsToDraw, this.configuration);

            canvas.add(this.currentDraw);
            canvas.renderAll();
        }
    }, {
        key: 'setText',
        value: function setText(text, canvas) {

            if (this.drawMode === "SHOW") {
                return;
            }

            if (this.figure) {
                this.figure.text = text;
            }

            if (this.currentDraw) {
                canvas.remove(this.currentDraw);
            }

            if (this.lineText) {
                canvas.remove(this.lineText);
            }

            var x1 = this.points[0].x;
            var x2 = this.points[1].x;
            var y1 = this.points[0].y;
            var y2 = this.points[1].y;

            var deltaY = y2 - y1;
            var deltaX = x2 - x1;

            //const pad = 1 / 2;

            //const angleInDegrees = Math.atan2(deltaY, deltaX) * 180 / Math.PI;
            var left = (x1 + x2) / 2;
            var top = (y1 + y2) / 2 + 5;
            this.lineText = _utils.Utils.createTextLabel(text, {
                left: left,
                top: top
            }, this.configuration);

            var group = new fabric.Group([this.currentDraw, this.lineText], {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true
            });
            canvas.add(group);
            this.parent.setFigure(this.figure.key, group);
        }
    }, {
        key: 'cancelDraw',
        value: function cancelDraw(canvas) {
            this.preventUp = true;
            this.points = [];
            if (this.currentDraw) {
                canvas.remove(this.currentDraw);
            }
        }
    }]);

    return RectDrawer;
}();

/***/ }),
/* 8 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.Ruler = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = __webpack_require__(0);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Ruler = exports.Ruler = function () {
    function Ruler(params) {
        _classCallCheck(this, Ruler);

        this.canvas = params.canvas;
        this.text = params.text;
        this.center = this.canvas.getCenter();
        this.configuration = params.rulerConfiguration;
        if (this.configuration && this.configuration.pixels > 0) {
            this.generateRuler();
        }
    }

    _createClass(Ruler, [{
        key: "generateRuler",
        value: function generateRuler() {
            switch (this.configuration.position) {
                case "LEFT":
                    this.generateLeftRuler();
                    break;
                case "RIGHT":
                    this.generateRightRuler();
                    break;
            }
        }
    }, {
        key: "generateRightRuler",
        value: function generateRightRuler() {

            var x12 = this.canvas.getWidth() - this.configuration.space;
            var y2 = this.center.top - this.configuration.pixels / 2;
            var y3 = this.center.top + this.configuration.pixels / 2;

            /*if (this.configuration.pixels < this.canvas.getHeight()) {
                y2 = this.center.top - (this.configuration.pixels / 2);
                y3 = this.center.top + (this.configuration.pixels / 2);
            }*/

            var line = _utils.Utils.createRulerLine([x12, y2, x12, y3], this.configuration);

            //this.canvas.add(line);

            var topLineCoords = [x12 - this.configuration.segmentSize, y2, x12 + this.configuration.strokeWidth, y2];
            var topLine = _utils.Utils.createRulerLine(topLineCoords, this.configuration);
            // this.canvas.add(topLine);

            var bottomLineCoords = [x12 - this.configuration.segmentSize, y3, x12 + this.configuration.strokeWidth, y3];
            var bottomLine = _utils.Utils.createRulerLine(bottomLineCoords, this.configuration);
            //this.canvas.add(bottomLine);

            var text = _utils.Utils.createTextLabel(this.text, {
                top: this.center.top,
                left: this.canvas.getWidth()
            }, this.configuration);
            text.set("objectCaching", false);

            text.set("top", this.center.top - text.getScaledHeight() / 2);
            text.set("left", this.canvas.getWidth() - (this.configuration.strokeWidth + 1.5 * this.configuration.segmentSize) - text.getScaledWidth());
            //this.canvas.add(text);

            var segmentLinesCoords = this.calculateRightPoints(x12, y2);
            var segmentLines = [];
            for (var i = 0; i < segmentLinesCoords.length; i++) {
                var segment = _utils.Utils.createRulerLine(segmentLinesCoords[i], this.configuration);
                segmentLines.push(segment);
            }

            this.group = new fabric.Group([line, topLine, bottomLine, text].concat(segmentLines), {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true,
                shadow: this.configuration.figureShadow
            });

            this.canvas.add(this.group);
            this.canvas.renderAll();
        }
    }, {
        key: "calculateRightPoints",
        value: function calculateRightPoints(x12, y2) {
            var step = this.configuration.pixels / this.configuration.split;
            var x1 = x12 - this.configuration.segmentSize / 2;

            var linesCoords = [];

            for (var i = 1; i < this.configuration.split; i++) {
                var y1 = y2 + i * step;
                linesCoords.push([x1, y1, x12, y1]);
            }

            return linesCoords;
        }
    }, {
        key: "generateLeftRuler",
        value: function generateLeftRuler() {

            var x12 = this.configuration.space;

            var y2 = this.center.top - this.configuration.pixels / 2;
            var y3 = this.center.top + this.configuration.pixels / 2;

            /*if (this.configuration.pixels < this.canvas.getHeight()) {
                y2 = this.center.top - (this.configuration.pixels / 2);
                y3 = this.center.top + (this.configuration.pixels / 2);
            }*/

            var line = _utils.Utils.createRulerLine([x12, y2, x12, y3], this.configuration);

            //this.canvas.add(line);

            var topLineCoords = [x12 + this.configuration.segmentSize, y2, x12, y2];
            var topLine = _utils.Utils.createRulerLine(topLineCoords, this.configuration);
            // this.canvas.add(topLine);

            var bottomLineCoords = [x12 + this.configuration.segmentSize, y3, x12, y3];
            var bottomLine = _utils.Utils.createRulerLine(bottomLineCoords, this.configuration);
            //this.canvas.add(bottomLine);

            var text = _utils.Utils.createTextLabel(this.text, {
                top: this.center.top,
                left: this.canvas.getWidth()
            }, this.configuration);
            text.set("objectCaching", false);

            text.set("top", this.center.top - text.getScaledHeight() / 2);
            text.set("left", this.configuration.strokeWidth + 1.5 * this.configuration.segmentSize);
            //this.canvas.add(text);

            var segmentLinesCoords = this.calculateLeftPoints(x12, y2);
            var segmentLines = [];
            for (var i = 0; i < segmentLinesCoords.length; i++) {
                var segment = _utils.Utils.createRulerLine(segmentLinesCoords[i], this.configuration);
                segmentLines.push(segment);
            }

            this.group = new fabric.Group([line, topLine, bottomLine, text].concat(segmentLines), {
                selectable: false,
                objectCaching: false,
                perPixelTargetFind: true,
                shadow: this.configuration.figureShadow
            });

            this.canvas.add(this.group);
            this.canvas.renderAll();
        }
    }, {
        key: "calculateLeftPoints",
        value: function calculateLeftPoints(x12, y2) {
            var step = this.configuration.pixels / this.configuration.split;
            var x1 = x12 + this.configuration.segmentSize / 2;

            var linesCoords = [];

            for (var i = 1; i < this.configuration.split; i++) {
                var y1 = y2 + i * step;
                linesCoords.push([x12, y1, x1, y1]);
            }

            return linesCoords;
        }
    }]);

    return Ruler;
}();

/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
    value: true
});
exports.FabricJsApp = undefined;

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _line = __webpack_require__(5);

var _angle = __webpack_require__(2);

var _freeAngle = __webpack_require__(4);

var _rect = __webpack_require__(7);

var _drawer = __webpack_require__(3);

var _utils = __webpack_require__(0);

var _loader = __webpack_require__(6);

var _ruler = __webpack_require__(8);

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var privateMethods = {
    /**
     * 
     * @param {type} name
     * @returns {String} Identificador
     */
    generateCanvasIds: function generateCanvasIds(name) {
        return name + "_" + Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15) + "_" + new Date().getTime();
    },

    /**
     * 
     * @param {type} options -  {canvasId:"",zindex:0, width:0, height:0, cursor:'none'}
     * @returns {com_vaadin_fabricjs_FabricJs.FabricJs.createCanvas.canvasHtml|Element}
     */
    createCanvas: function createCanvas(options) {

        var defaultOptions = {
            width: 0,
            height: 0,
            zindex: 0,
            cursor: "default"
        };

        var actualOptions = Object.assign({}, defaultOptions, options);

        var canvasHtml = document.createElement('canvas');
        canvasHtml.id = actualOptions.canvasId;
        canvasHtml.width = actualOptions.width;
        canvasHtml.height = actualOptions.height;
        canvasHtml.style.position = "absolute";
        canvasHtml.style.left = 0;
        canvasHtml.style.top = 0;
        canvasHtml.style.zindex = actualOptions.zindex;
        canvasHtml.style.cursor = actualOptions.cursor;
        return canvasHtml;
    },
    containerDiv: function containerDiv() {
        var _div = document.createElement("div");
        _div.style.position = "absolute";
        _div.style.width = 0;
        _div.style.height = 0;
        _div.style.left = 0;
        _div.style.top = 0;
        _div.style.cursor = "none";
        _div.style.outline = "none";
        return _div;
    }
};

var FabricJsApp = exports.FabricJsApp = function () {
    function FabricJsApp(options) {
        _classCallCheck(this, FabricJsApp);

        this.options = options;
        this.configuration = this.options.configuration;
        this.loaderConfiguration = this.options.loaderConfiguration;
        this.rulerConfiguration = this.rulerConfiguration;
        this.figures = new Map();
        this.notes = new Map();
        this.showSpinnerOnImageLoad = options.showSpinnerOnImageLoad;

        this.offCanvas = false;

        this._currentAction = this.options.action;
        this.options.container.style.backgroundColor = this.configuration.backgroundColor;

        var canvasImageId = privateMethods.generateCanvasIds("canvas_image");
        var canvasNotesId = privateMethods.generateCanvasIds("canvas_notes");
        var canvasDrawId = privateMethods.generateCanvasIds("canvas_draw");
        var canvasLoaderId = privateMethods.generateCanvasIds("canvas_loader");

        var htmlCanvasImage = privateMethods.createCanvas({
            canvasId: canvasImageId
        });
        var htmlCanvasNotes = privateMethods.createCanvas({
            canvasId: canvasNotesId,
            zindex: 1
        });

        var htmlCanvasLoader = privateMethods.createCanvas({
            canvasId: canvasLoaderId,
            zindex: 2
        });

        //ponerlo en un div absolute con tamaño igual que la imagen
        this._div = privateMethods.containerDiv();
        this._divContainer = privateMethods.containerDiv();
        this._divContainer.style.width = "100%";
        this._divContainer.style.height = "100%";
        this._div.style.zIndex = 4;

        var htmlCanvasDraw = privateMethods.createCanvas({
            canvasId: canvasDrawId,
            zindex: 3,
            cursor: "pointer"
        });

        this._divContainer.appendChild(htmlCanvasImage);
        this._divContainer.appendChild(htmlCanvasNotes);
        this._divContainer.appendChild(htmlCanvasLoader);
        this._divContainer.appendChild(this._div);
        this._div.appendChild(htmlCanvasDraw);

        this.options.container.appendChild(this._divContainer);

        this.canvasImage = new fabric.StaticCanvas(canvasImageId, {
            backgroundColor: 'transparent',
            allowTouchScrolling: true
        });

        this.canvasNotes = new fabric.StaticCanvas(canvasNotesId, {
            backgroundColor: 'transparent',
            allowTouchScrolling: true
        });

        this.canvasLoader = new fabric.StaticCanvas(canvasLoaderId, {
            backgroundColor: 'transparent',
            allowTouchScrolling: true
        });

        this.loader = new _loader.Loader({
            canvas: this.canvasLoader,
            loaderConfiguration: this.loaderConfiguration
        });

        this.canvasDraw = new fabric.Canvas(canvasDrawId, {
            selection: false,
            backgroundColor: 'transparent',
            allowTouchScrolling: true
        });

        var _this = this;
        this.canvasDraw.on("mouse:down", function (o) {
            if (_this.currentDraw) {
                _this.isMouseDown = true;
                _this.rect = _this.canvasDraw.calcViewportBoundaries();

                if (_this.offCanvas) {
                    _this.offCanvas = false;
                    return;
                }

                if (!_this.loadedImage) {
                    return;
                }
                _this.currentDraw.onMouseDown(o, _this.canvasDraw);
            }
        });

        this.canvasDraw.on('mouse:move', function (o) {
            if (_this.currentDraw) {

                var pointer = _this.canvasDraw.getPointer(o.e);

                if (!_this.isMouseDown && !_this.offCanvas) {
                    return;
                }

                if (!_this.loadedImage) {
                    return;
                }

                if (pointer.x < 0 || pointer.y < 0 || pointer.x > _this.rect.br.x || pointer.y > _this.rect.br.y) {
                    _this.offCanvas = true;
                    return;
                }

                _this.currentDraw.onMouseMove(o, _this.canvasDraw);
            }
        });

        this.canvasDraw.on("mouse:up", function (o) {
            if (_this.currentDraw) {
                _this.isMouseDown = false;

                if (_this.offCanvas) {
                    return;
                }

                if (!_this.loadedImage) {
                    return;
                }

                _this.currentDraw.onMouseUp(o, _this.canvasDraw);
            }
        });

        var processGroup = function processGroup(target, color) {

            var objects = target.getObjects();
            for (var o in objects) {
                switch (objects[o].type) {
                    case "line":
                        objects[o].set('stroke', color);
                        break;
                    case "rect":
                        objects[o].set('stroke', color);
                        break;
                    case "text":
                        objects[o].set('fill', color);
                        break;
                    case "group":
                        processGroup(objects[o], color);
                        break;
                }
            }
        };

        this.canvasDraw.on('mouse:over', function (e) {
            if (_this.configuration.hoverColor && e.target) {
                var target = e.target;
                if (target.type === "group") {
                    processGroup(target, _this.configuration.hoverColor);
                }
                _this.canvasDraw.renderAll();
            }
        });

        this.canvasDraw.on('mouse:out', function (e) {
            if (_this.configuration.hoverColor && e.target) {
                var target = e.target;
                if (target.type === "group") {
                    processGroup(target, _this.configuration.fillColor);
                }
                _this.canvasDraw.renderAll();
            }
        });

        var handleScroll = function handleScroll(e) {
            var delta = 0;

            if (e.wheelDelta) {
                if (e.wheelDelta < 0) delta = 1;else delta = -1;
            } else if (e.detail < 0) delta = -1;else delta = 1;

            _this.options.component.onMouseWheel(delta);
        };

        this.canvasContainer = document.getElementById(canvasDrawId).parentElement;

        this.canvasContainer.addEventListener('DOMMouseScroll', handleScroll, false); // For Firefox
        this.canvasContainer.addEventListener('mousewheel', handleScroll, false);

        this.wrapper = this.canvasDraw.wrapperEl;
        this.wrapper.tabIndex = 1000;

        this.wrapper.addEventListener("keyup", function (event) {
            if (event.which === _this.configuration.cancelDrawKeyCode) {
                if (_this.currentDraw) {
                    _this.currentDraw.cancelDraw(_this.canvasDraw);
                }
            }
        }, false);
    }

    _createClass(FabricJsApp, [{
        key: 'activeTouchActions',
        value: function activeTouchActions() {
            this._div.style.touchAction = "auto";
            this.canvasContainer.style.touchAction = "auto";
            this.wrapper.style.touchAction = "auto";
            this.wrapper.childNodes.item(0).style.touchAction = "auto";
            this.wrapper.childNodes.item(1).style.touchAction = "auto";
        }
    }, {
        key: 'disableTouchActions',
        value: function disableTouchActions() {
            this._div.style.touchAction = "none";
            this.canvasContainer.style.touchAction = "none";
            this.wrapper.style.touchAction = "none";
            this.wrapper.childNodes.item(0).style.touchAction = "none";
            this.wrapper.childNodes.item(1).style.touchAction = "none";
        }
    }, {
        key: 'setAction',
        value: function setAction(action) {
            this._currentAction = action;
            this.disableTouchActions();
            if (this.currentDraw) {
                this.currentDraw.cancelDraw(this.canvasDraw);
            }
            switch (this._currentAction) {
                case "DRAW_LINE":
                    this.currentDraw = new _line.LineDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "DRAW",
                        parent: this
                    });
                    break;
                case "DRAW_ANGLE":
                    this.currentDraw = new _angle.AngleDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "DRAW",
                        parent: this
                    });
                    break;
                case "DRAW_FREE_ANGLE":
                    this.currentDraw = new _freeAngle.FreeAngleDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "DRAW",
                        parent: this
                    });
                    break;
                case "DRAW_RECT":
                    this.currentDraw = new _rect.RectDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "DRAW",
                        parent: this
                    });
                    break;
                case "SHOW_LINE":
                    this.currentDraw = new _line.LineDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "SHOW",
                        parent: this
                    });
                    break;
                case "SHOW_ANGLE":
                    this.currentDraw = new _angle.AngleDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "SHOW",
                        parent: this
                    });
                    break;
                case "SHOW_RECT":
                    this.currentDraw = new _rect.RectDrawer({
                        configuration: this.configuration,
                        component: this.options.component,
                        drawMode: "SHOW",
                        parent: this
                    });
                    break;
                case "NONE":
                    this.activeTouchActions();
                    break;
                default:
                    this.currentDraw = null;
                    break;
            }
        }
    }, {
        key: 'draw',
        value: function draw(figure) {
            switch (figure.figureType) {
                case "LINE":
                    _drawer.Drawer.drawLine(figure, this.canvasDraw, figure.configuration ? figure.configuration : this.configuration);
                    break;
                case "ANGLE":
                    _drawer.Drawer.drawAngle(figure, this.canvasDraw, figure.configuration ? figure.configuration : this.configuration);
                    break;
                case "FREE_ANGLE":
                    _drawer.Drawer.drawFreeAngle(figure, this.canvasDraw, figure.configuration ? figure.configuration : this.configuration);
                    break;
                case "RECT":
                    _drawer.Drawer.drawRect(figure, this.canvasDraw, figure.configuration ? figure.configuration : this.configuration);
                    break;
                case "RULER":
                    if (this.ruler && this.ruler.group) {
                        this.canvasNotes.remove(this.ruler.group);
                    }
                    this.ruler = new _ruler.Ruler({
                        text: figure.text,
                        rulerConfiguration: figure.configuration ? figure.configuration : this.loaderConfiguration,
                        canvas: this.canvasNotes
                    });
                    break;
            }
        }
    }, {
        key: 'setText',
        value: function setText(figure) {
            if (this.currentDraw) {
                this.currentDraw.setText(figure.text, this.canvasDraw);
            }
        }
    }, {
        key: 'setShowSpinnerOnImageLoad',
        value: function setShowSpinnerOnImageLoad(showSpinnerOnImageLoad) {
            this.showSpinnerOnImageLoad = showSpinnerOnImageLoad;
        }
    }, {
        key: 'setConfiguration',
        value: function setConfiguration(configuration) {
            this.configuration = configuration;
            this.setAction(this.configuration.action);
            this.setCursor(this.configuration.cursor);
            if (this.currentDraw) {
                this.currentDraw.setConfiguration(this.configuration);
            }
        }
    }, {
        key: 'setRulerConfiguration',
        value: function setRulerConfiguration(rulerConfiguration) {
            this.rulerConfiguration = rulerConfiguration;
        }
    }, {
        key: 'setLoaderConfiguration',
        value: function setLoaderConfiguration(loaderConfiguration) {
            this.loaderConfiguration = loaderConfiguration;
        }
    }, {
        key: 'setDimensions',
        value: function setDimensions(dimensions) {
            this.canvasNotes.setDimensions(dimensions);
            this.canvasImage.setDimensions(dimensions);
            this.canvasDraw.setDimensions(dimensions);
            this.canvasLoader.setDimensions(dimensions);
            this._div.style.width = dimensions.width;
            this._div.style.height = dimensions.height;
        }
    }, {
        key: 'setNotesCanvasDimensions',
        value: function setNotesCanvasDimensions(dimensions) {
            this.canvasNotes.setDimensions(dimensions);
        }
    }, {
        key: 'setBackgroundImage',
        value: function setBackgroundImage(url, afterLoadImage) {
            var _this = this;
            fabric.Image.fromURL(url, function (oImg) {
                _this.canvasImage.clear();

                _this.loadedImage = oImg;

                var _width = _this.loadedImage.getScaledWidth();
                var _height = _this.loadedImage.getScaledHeight();

                var _widthContainer = _this.options.container.offsetWidth;
                var _heightContainer = _this.options.container.offsetHeight;

                _this._dx = (_widthContainer - _width) / 2;
                _this._dy = (_heightContainer - _height) / 2;

                _this._divContainer.style.width = _widthContainer;
                _this._divContainer.style.height = _heightContainer;

                /**
                 * Agregando la imagen
                 */
                _this.canvasImage.add(_this.loadedImage).setWidth(_widthContainer).setHeight(_heightContainer);
                _this.loadedImage.center();
                _this.loadedImage.setCoords();
                _this.canvasImage.renderAll();

                _this.canvasLoader.setWidth(_widthContainer).setHeight(_heightContainer);

                _this.canvasDraw.setWidth(_width).setHeight(_height);
                _this._div.style.width = _width + "px";
                _this._div.style.height = _height + "px";
                _this._div.style.position = "relative";
                _this._div.style.marginTop = "0px";
                _this._div.style.marginBottom = "0px";
                _this._div.style.marginLeft = "auto";
                _this._div.style.marginRight = "auto";
                _this._div.style.top = _this._dy + "px";

                _this.canvasDraw.renderAll();

                _this.canvasNotes.setWidth(_widthContainer).setHeight(_heightContainer);
                _this.canvasNotes.renderAll();

                if (_this.showSpinnerOnImageLoad) {
                    _this.hideLoader();
                }
                if (afterLoadImage) {
                    afterLoadImage();
                }
            });
        }
    }, {
        key: 'onLoadImage',
        value: function onLoadImage(imagesUrl, afterLoadImage) {
            if (imagesUrl.length) {
                if (this.showSpinnerOnImageLoad) {
                    this.showLoader();
                }
                if (imagesUrl.length === 1) {
                    this.setBackgroundImage(imagesUrl[0], afterLoadImage);
                }
            }
        }
    }, {
        key: 'executeChainOfCommand',
        value: function executeChainOfCommand(cc) {
            var _this = this;
            if (cc.clearAll) {
                this.clear();
            } else {
                if (cc.clearDraw) {
                    this.clearDraw();
                }

                if (cc.clearImage) {
                    this.clearImage();
                }

                if (cc.clearNotes) {
                    this.clearNotes();
                }
            }

            if (cc.figureConfiguration) {
                this.setConfiguration(cc.figureConfiguration);
            }

            if (cc.loaderConfiguration) {
                this.setLoaderConfiguration(cc.loaderConfiguration);
            }

            if (cc.notes) {
                for (var i = 0; i < cc.notes.length; i++) {
                    this.addNotes(cc.notes[i].key, cc.notes[i].text, cc.notes[i].notesConfiguration);
                }
            }

            if (cc.imagesUrl) {
                this.onLoadImage(cc.imagesUrl, function () {
                    if (cc.figures) {
                        for (var _i = 0; _i < cc.figures.length; _i++) {
                            this.draw(cc.figures[_i]);
                        }
                    }
                }.bind(this));
            } else {
                if (cc.figures) {
                    for (var _i2 = 0; _i2 < cc.figures.length; _i2++) {
                        this.draw(cc.figures[_i2]);
                    }
                }
            }
        }
    }, {
        key: 'addNotes',
        value: function addNotes(key, note, configuration) {
            var text = _utils.Utils.createTextNotes(note, configuration);
            var oldNote = this.notes.get(key);

            if (oldNote) {
                this.canvasNotes.remove(oldNote);
            }

            this.canvasNotes.add(text);
            this.notes.set(key, text);

            _utils.Utils.calculateOriginY(this.canvasNotes, text, configuration.originX, configuration.originY);
            _utils.Utils.calculateOriginX(this.canvasNotes, text, configuration.originX, configuration.originY);

            text.setCoords();

            this.canvasNotes.renderAll();
        }
    }, {
        key: 'setFigure',
        value: function setFigure(key, figure) {
            this.figures.set(key, figure);
        }
    }, {
        key: 'removeFigure',
        value: function removeFigure(key) {
            this.canvasDraw.remove(this.figures.get(key));
            this.figures.delete(key);
            this.canvasDraw.renderAll();
        }
    }, {
        key: 'removeNote',
        value: function removeNote(key) {
            this.canvasNotes.remove(this.notes.get(key));
            this.notes.delete(key);
            this.canvasNotes.renderAll();
        }
    }, {
        key: 'drawLine',
        value: function drawLine(x1, y1, x2, y2, text) {
            if (!this.loadedImage) {
                return;
            }

            var points = [x1, y1, x2, y2];

            this.currentLine = new fabric.Line(points, {
                strokeWidth: this.configuration.strokeWidth,
                fill: this.configuration.fillColor,
                stroke: this.configuration.strokeColor,
                originX: 'center',
                originY: 'center',

                selectable: false
            });

            if (text) {

                if (this.lineText) {
                    this.canvasDraw.remove(this.lineText);
                }

                var deltaY = y2 - y1;
                var deltaX = x2 - x1;

                this.lineText = new fabric.Text(text, {
                    left: (x1 + x2) / 2, //Take the block's position
                    top: (y1 + y2) / 2 + 5,
                    backgroundColor: 'transparent',
                    fill: this.configuration.fillColor,
                    stroke: this.configuration.strokeColor,
                    fontWeight: this.configuration.textFontWeight,
                    fontStyle: this.configuration.textFontStyle,
                    fontSize: this.configuration.textFontSize,
                    fontFamily: this.configuration.textFontFamily,
                    selectable: false
                });

                var group = new fabric.Group([this.currentLine, this.lineText], {
                    selectable: false
                });
                this.canvasDraw.add(group);
            } else {
                this.canvasDraw.add(this.currentLine);
            }
            this.canvasDraw.renderAll();
        }
    }, {
        key: 'clear',
        value: function clear() {
            this.canvasNotes.clear();
            this.canvasImage.clear();
            this.canvasDraw.clear();
        }
    }, {
        key: 'clearNotes',
        value: function clearNotes() {
            this.canvasNotes.clear();
        }
    }, {
        key: 'clearImage',
        value: function clearImage() {
            this.canvasImage.clear();
        }
    }, {
        key: 'clearDraw',
        value: function clearDraw() {
            this.canvasDraw.clear();
        }
    }, {
        key: 'setCursor',
        value: function setCursor(cursor) {
            this.canvasDraw.setCursor(cursor);
            this.canvasDraw.defaultCursor = cursor;
            this.canvasDraw.hoverCursor = cursor;
            this.canvasDraw.moveCursor = cursor;
            this.canvasDraw.rotationCursor = cursor;
            this.canvasDraw.freeDrawingCursor = cursor;
        }
    }, {
        key: 'showLoader',
        value: function showLoader() {
            this.loader.showLoader();
        }
    }, {
        key: 'hideLoader',
        value: function hideLoader() {
            this.loader.hideLoader();
        }
    }]);

    return FabricJsApp;
}();

/***/ })
/******/ ]);
});