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
/******/ 	return __webpack_require__(__webpack_require__.s = 7);
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
                objectCaching: false
            });
            return rect;
        }
    }, {
        key: 'createTextLabel',
        value: function createTextLabel(text, options, configuration) {
            var textObject = new fabric.Text(text, {
                backgroundColor: 'transparent',
                fill: configuration.textFillColor,
                fontWeight: configuration.textFontWeight,
                fontStyle: configuration.textFontStyle,
                fontSize: configuration.textFontSize,
                fontFamily: configuration.textFontFamily,
                left: options.left,
                top: options.top,
                visible: configuration.visible,
                selectable: false,
                objectCaching: false
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
                objectCaching: false
            });
            return textObject;
        }
    }, {
        key: 'generateUniqueId',
        value: function generateUniqueId() {
            return 'obj_' + Math.random().toString(36).substr(2, 16) + "_" + new Date().getTime();
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

                this.figure = new _figure.Figure(_utils.Utils.generateUniqueId(), "ANGLE", this.points, "", this.drawMode === "DRAW");

                if (this.drawMode === "SHOW") {
                    canvas.remove(this.currentDraw);
                    canvas.renderAll();
                } else {
                    this.angle = new fabric.Group([this.firstLine, this.secondLine]);
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
                objectCaching: false
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
                objectCaching: false
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
                objectCaching: false
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
                objectCaching: false
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
                objectCaching: false
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
                objectCaching: false
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

                if (pointer.x < 0 || pointer.y < 0 || pointer.x > this.rect.br.x || pointer.y > this.rect.br.y) {
                    return;
                }

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
            this.rect = canvas.calcViewportBoundaries();
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
                objectCaching: false
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
                objectCaching: false
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
/* 7 */
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

var _rect = __webpack_require__(6);

var _drawer = __webpack_require__(3);

var _utils = __webpack_require__(0);

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
    },
    calculateOriginX: function calculateOriginX(canvas, text, originX, originY) {

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
    },
    calculateOriginY: function calculateOriginY(canvas, text, originX, originY) {
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
};

var FabricJsApp = exports.FabricJsApp = function () {
    function FabricJsApp(options) {
        _classCallCheck(this, FabricJsApp);

        this.options = options;
        this.configuration = this.options.configuration;
        this.figures = new Map();
        this.notes = new Map();

        this._currentAction = this.options.action;
        this.options.container.style.backgroundColor = this.configuration.backgroundColor;

        var canvasImageId = privateMethods.generateCanvasIds("canvas_image");
        var canvasNotesId = privateMethods.generateCanvasIds("canvas_notes");
        var canvasDrawId = privateMethods.generateCanvasIds("canvas_draw");

        var htmlCanvasImage = privateMethods.createCanvas({
            canvasId: canvasImageId
        });
        var htmlCanvasNotes = privateMethods.createCanvas({
            canvasId: canvasNotesId,
            zindex: 1
        });

        //ponerlo en un div absolute con tamaño igual que la imagen
        this._div = privateMethods.containerDiv();
        this._divContainer = privateMethods.containerDiv();
        this._divContainer.style.width = "100%";
        this._divContainer.style.height = "100%";

        var htmlCanvasDraw = privateMethods.createCanvas({
            canvasId: canvasDrawId,
            zindex: 2,
            cursor: "pointer"
        });

        this._divContainer.appendChild(htmlCanvasImage);
        this._divContainer.appendChild(htmlCanvasNotes);
        this._divContainer.appendChild(this._div);
        this._div.appendChild(htmlCanvasDraw);

        this.options.container.appendChild(this._divContainer);

        this.canvasImage = new fabric.StaticCanvas(canvasImageId, {
            backgroundColor: 'transparent'
        });

        this.canvasNotes = new fabric.StaticCanvas(canvasNotesId, {
            backgroundColor: 'transparent'
        });

        this.canvasDraw = new fabric.Canvas(canvasDrawId, {
            selection: false,
            backgroundColor: 'transparent'
        });

        var _this = this;
        this.canvasDraw.on("mouse:down", function (o) {
            _this.isMouseDown = true;

            if (!_this.loadedImage) {
                return;
            }

            if (_this.currentDraw) {
                _this.currentDraw.onMouseDown(o, _this.canvasDraw);
            }
        });

        this.canvasDraw.on('mouse:move', function (o) {
            if (!_this.isMouseDown) return;

            if (!_this.loadedImage) {
                return;
            }

            if (_this.currentDraw) {
                _this.currentDraw.onMouseMove(o, _this.canvasDraw);
            }
        });

        this.canvasDraw.on("mouse:up", function (o) {
            _this.isMouseDown = false;

            if (!_this.loadedImage) {
                return;
            }

            if (_this.currentDraw) {
                _this.currentDraw.onMouseUp(o, _this.canvasDraw);
            }
        });

        /*this.canvasDraw.on("mouse:wheel", function (o) {
            var e = o.e;
            var delta = 0;
             if (e.wheelDelta) {
                if (e.wheelDelta < 0)
                    delta = 1;
                else
                    delta = -1;
            } else if (e.deltaY < 0)
                delta = -1;
            else
                delta = 1;
             _this.options.component.onMouseWheel(delta);
        });*/

        var handleScroll = function handleScroll(e) {
            var delta = 0;

            if (e.wheelDelta) {
                if (e.wheelDelta < 0) delta = 1;else delta = -1;
            } else if (e.detail < 0) delta = -1;else delta = 1;

            _this.options.component.onMouseWheel(delta);
        };

        var canvasContainer = document.getElementsByClassName("canvas-container")[0];
        canvasContainer.addEventListener('DOMMouseScroll', handleScroll, false); // For Firefox
        canvasContainer.addEventListener('mousewheel', handleScroll, false);

        var wrapper = this.canvasDraw.wrapperEl;
        wrapper.tabIndex = 1000;

        wrapper.addEventListener("keyup", function (event) {
            if (event.which === 27) {
                if (_this.currentDraw) {
                    _this.currentDraw.cancelDraw(_this.canvasDraw);
                }
            }
        }, false);
    }

    _createClass(FabricJsApp, [{
        key: 'setAction',
        value: function setAction(action) {
            this._currentAction = action;
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
                    _drawer.Drawer.drawLine(figure, this.canvasDraw, figure.configuration);
                    break;
                case "ANGLE":
                    _drawer.Drawer.drawAngle(figure, this.canvasDraw, figure.configuration);
                    break;
                case "FREE_ANGLE":
                    _drawer.Drawer.drawFreeAngle(figure, this.canvasDraw, figure.configuration);
                    break;
                case "RECT":
                    _drawer.Drawer.drawRect(figure, this.canvasDraw, figure.configuration);
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
        key: 'setDimensions',
        value: function setDimensions(dimensions) {
            this.canvasNotes.setDimensions(dimensions);
            this.canvasImage.setDimensions(dimensions);
            this.canvasDraw.setDimensions(dimensions);
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
        value: function setBackgroundImage(url) {
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
            });
        }
    }, {
        key: 'onLoadImage',
        value: function onLoadImage(imageUrls) {
            if (imageUrls.length) {
                if (imageUrls.length === 1) {
                    this.setBackgroundImage(imageUrls[0]);
                }
            }
        }
    }, {
        key: 'addNotes',
        value: function addNotes(key, note, configuration) {
            var text = _utils.Utils.createTextNotes(note, configuration);
            this.canvasNotes.add(text);
            this.notes.set(key, text);

            privateMethods.calculateOriginY(this.canvasNotes, text, configuration.originX, configuration.originY);
            privateMethods.calculateOriginX(this.canvasNotes, text, configuration.originX, configuration.originY);

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
    }]);

    return FabricJsApp;
}();

/***/ })
/******/ ]);
});