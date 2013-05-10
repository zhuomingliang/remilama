var global = this;
var render = (function(global, content) {
  var docStyle = document.documentElement.style;

  var engine;
  if (global.opera && Object.prototype.toString.call(opera) === '[object Opera]') {
    engine = 'presto';
  } else if ('MozAppearance' in docStyle) {
    engine = 'gecko';
  } else if ('WebkitAppearance' in docStyle) {
    engine = 'webkit';
  } else if (typeof navigator.cpuClass === 'string') {
    engine = 'trident';
  }

  var vendorPrefix = {
    trident: 'ms',
    gecko: 'Moz',
    webkit: 'Webkit',
    presto: 'O'
  }[engine];

  var helperElem = document.createElement("div");
  var undef;

  var perspectiveProperty = vendorPrefix + "Perspective";
  var transformProperty = vendorPrefix + "Transform";

  if (helperElem.style[perspectiveProperty] !== undef) {
    
    return function(left, top, zoom) {
      content.css(transformProperty, 'translate3d(' + (-left) + 'px,' + (-top) + 'px,0) scale(' + zoom + ')');
    };

  } else if (helperElem.style[transformProperty] !== undef) {
    return function(left, top, zoom) {
      content.css(transformProperty, 'translate(' + (-left) + 'px,' + (-top) + 'px) scale(' + zoom + ')');
    };
  } else {
    return function(left, top, zoom) {
      content.css({
        marginLeft: left ? (-left/zoom) + 'px' : '',
        marginTop:  top ? (-top/zoom) + 'px' : '',
        zoom: zoom || ''
      });
    };
  }
});

var Document = Backbone.Model.extend({
});

var Comment = Backbone.Model.extend({
});

var CommentList = Backbone.Collection.extend({
  model: Comment
});

var Page = Backbone.Model.extend({
  initialize: function() {
    this.commentList = new CommentList();
  }
});

var DocumentView = Backbone.View.extend({
  events: {
    "click .pagination li a": "onSwitchPage"
  },
  el: "body",
  initialize: function() {
    var self = this;
    this.documentId = this.options["documentId"];
    this.pageList = _.map(_.range(1, $(".pagination li").size() + 1), function(n) {
      return new Page({documentId: self.documentId, pageNo: n});
    });
    this.pageView = new PageView({model: _.first(this.pageList)});

  },
  onSwitchPage: function(e) {
    this.pageView.pageNo = Number($(e.target).text());
    this.pageView.model = this.pageList[this.pageView.pageNo - 1];
    this.pageView.render();
    return false;
  }
});

var PageView = Backbone.View.extend({
  el: ".page-container",
  events: {
    "mousewheel": 'onMouseZoom',
    "mousedown":  'onMouseDown',
    "mousemove":  'onMouseMove',
    "mouseup":    'onMouseUp',
    "dblclick":   'onComment'
  },
  initialize: function() {
    var self = this;
    this.pageNo = 1;
    this.canvas = $(".page-content", this.el);
    this.svgCache = {};
    this.dragContainer = $(".drag-container", this.el);
    this.commentDialog = new CommentDialogView({
      pageView: this,
      model: this.model.commentList
    });
    this.scroller = new Scroller(render(global, this.dragContainer), {
      zooming: true
    });
    var rect = this.el.getBoundingClientRect();
    this.scroller.setPosition(rect.left + this.el.clientLeft,
                              rect.top + this.el.clientTop);
    this.render();
    this.mousedown = true;
  },
  render: function() {
    var self = this;
    /*
    canvg(self.canvas[0], "/document/show/" + 
          this.model.get("documentId") + "/" +
          this.model.get("pageNo") + ".svg", {
      ignoreMouse: false, ignoreAnimation: false
    });
    self.scroller.setDimensions(self.$el.width(), self.$el.height(),
                                self.canvas.width(), self.canvas.height());
    */

    if (this.svgCache[self.model.get("pageNo")]) {
      this.renderPage(this.svgCache[self.model.get("pageNo")]);
    } else {
      $.get('/document/show/' + this.model.get("documentId") + "/" + this.model.get("pageNo") + ".svg", function(data) {
        var rootElement = document.importNode(data.documentElement, true);
        self.svgCache[self.model.get("pageNo")] = rootElement;
        self.renderPage(rootElement);
      }, "xml");
    }
    $(".pin", this.dragContainer).remove();
    this.model.commentList.each(function(comment) {
      self.dragContainer.append(new CommentView({
        model: comment, pageView: self
      }).render().el);
    });
    this.model.commentList.off("add").on("add", function(comment) {
      self.dragContainer.append(new CommentView({
        model: comment, pageView: self
      }).render().el);
    });
  },
  renderPage: function(svg) {
      this.canvas.width(svg.width.baseVal.value)
        .height(svg.height.baseVal.value);
      this.canvas.empty().append(svg);
      this.scroller.setDimensions(this.$el.width(), this.$el.height(),
                                  this.canvas.width(), this.canvas.height());
  },
  onMouseZoom: function(e) {
    this.scroller.doMouseZoom(e.originalEvent.wheelDelta,
                              e.timeStamp,
                              e.originalEvent.pageX,
                              e.originalEvent.pageY);
    return false;
  },
  onMouseDown: function(e) {
    if (e.target.tagName.match(/input|textarea|select/i)) {
      return;
    }
    this.scroller.doTouchStart([{
      pageX: e.pageX,
      pageY: e.pageY
    }], e.timeStamp);
    this.mousedown = true;
  },
  onMouseMove: function(e) {
    if (!this.mousedown) {
      return;
    }
    this.scroller.doTouchMove([{
      pageX: e.pageX,
      pageY: e.pageY
    }], e.timeStamp);
    this.mousedown = true;    
  },
  onMouseUp: function(e) {
    if (!this.mousedown) {
      return;
    }
    this.scroller.doTouchEnd(e.timeStamp);
    this.mousedown = false;
  },
  onComment: function(e) {
    var offset = this.$el.offset();
    this.dragContainer.css("transform").match(/matrix\(([0-9\.\-]+)\s*,\s*([0-9\.\-]+)\s*,\s*([0-9\.\-]+)\s*,\s*([0-9\.\-]+)\s*,\s*([0-9\.\-]+)\s*,\s*([0-9\.\-]+)\s*\)/);
    var scaleX = (RegExp.$1 || 1),
        scaleY = (RegExp.$4 || 1),
        offsetX = (RegExp.$5 || 0),
        offsetY = (RegExp.$6 || 0);

    this.commentDialog.show(new Comment({
      posX: (e.originalEvent.pageX - offset.left - offsetX) / scaleX,
      posY: (e.originalEvent.pageY - offset.top - offsetY)  / scaleY
    }));
  }
});

var CommentView = Backbone.View.extend({
  tagName: 'img',
  events: {
    "click": "showDialog"
  },
  initialize: function() {
    this.pageView = this.options["pageView"];
    this.model.bind("change", this.render, this);
  },
  render: function() {
    $(this.el)
      .attr("src", "/images/pin.png")
      .addClass("pin")
      .css({"left": (this.model.get("posX") - 16) + "px",
            "top":  (this.model.get("posY") - 32)+ "px"})
      .tooltip({
        title: this.model.get("description")
      });
    return this;
  },
  showDialog: function() {
    this.pageView.commentDialog.show(this.model);
  }
});

var CommentDialogView = Backbone.View.extend({
  el: ".dialog-comment",
  events: {
    "click .btn-save": "onSave"
  },
  initialize: function(options) {
    this.pageView = this.options["pageView"];
  },
  show: function(comment) {
    this.comment = comment;
    $(":input[name=description]", this.el).val(comment.get("description"));
    this.$el.modal('show');
  },
  onSave: function(e) {
    var description = $(":input[name=description]", this.el).val();
    if (description != "") {
      this.comment.set("description", description);
      if (!this.pageView.model.commentList.contains(this.comment))
        this.pageView.model.commentList.add(this.comment);
    }
    this.$el.modal('hide');
  }
});

window.Router = Backbone.Router.extend({
  routes: {
    "document/show/:documentId": "documentShow"
  },
  documentShow: function(documentId) {
    var documentView = new DocumentView({documentId: documentId});
  }
});

$(function() {
  var app = new Router();
  Backbone.history.start({pushState: true, hashChange: false});
});