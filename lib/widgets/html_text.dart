import 'package:flutter/widgets.dart';
import 'package:flutter/material.dart';

const double _defFontSize = 14;

class HtmlText extends StatelessWidget {
  final String data;
  final TextStyle? textStyle;

  // =================================================================================================================

  HtmlText({
    required this.data,
    this.textStyle
  });

  // =================================================================================================================

  @override
  Widget build(BuildContext context) {
    final textTheme = Theme.of(context).textTheme;
    final fontSize = textTheme.bodyText1?.fontSize ?? textTheme.caption?.fontSize ?? _defFontSize;
    final textColor = textTheme.bodyText1?.color ?? textTheme.button?.color ?? Colors.black;

    TextStyle baseTextStyle = TextStyle(
      color: textColor,
      fontSize: fontSize,
    );

    _HtmlParser parser = _HtmlParser(
      textStyle: baseTextStyle.merge(this.textStyle)
    );
    List nodes = parser.parse(this.data);

    return Container(
      child: RichText(
          text: TextSpan(
              text: '',
              style: DefaultTextStyle.of(context).style,
              children: nodes.map((node) => TextSpan(
                  text: node['text'],
                  style: node['style']
              )).toList()
          )
      ),
    );
  }
}

// =====================================================================================================================

class _HtmlParser {
  // Regular Expressions for parsing tags and attributes
  final RegExp _startTag = RegExp(r'^<([-A-Za-z0-9_]+)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")' + "|(?:'[^']*')|[^>\s]+))?)*)\s*(\/?)>");
  final RegExp _endTag = RegExp("^<\/([-A-Za-z0-9_]+)[^>]*>");
  final RegExp _attr = RegExp(r'([-A-Za-z0-9_]+)(?:\s*=\s*(?:(?:"((?:\\.|[^"])*)")' + r"|(?:'((?:\\.|[^'])*)')|([^>\s]+)))?");
  final RegExp _style = RegExp(r'([a-zA-Z\-]+)\s*:\s*([^;]*)');
  final RegExp _color = RegExp(r'^#([a-fA-F0-9]{6})$');

  final List _emptyTags     = const ['area', 'base', 'basefont', 'br', 'col', 'frame', 'hr', 'img', 'input',
    'isindex', 'link', 'meta', 'param', 'embed'];
  final List _blockTags     = const ['address', 'applet', 'blockquote', 'button', 'center', 'dd', 'del', 'dir',
    'div', 'dl', 'dt', 'fieldset', 'form', 'frameset', 'hr', 'iframe', 'ins',
    'isindex', 'li', 'map', 'menu', 'noframes', 'noscript', 'object', 'ol',
    'p', 'pre', 'script', 'table', 'tbody', 'td', 'tfoot', 'th', 'thead',
    'tr', 'ul'];
  final List _inlineTags    = const ['a', 'abbr', 'acronym', 'applet', 'b', 'basefont', 'bdo', 'big', 'br', 'button',
    'cite', 'code', 'del', 'dfn', 'em', 'font', 'i', 'iframe', 'img', 'input',
    'ins', 'kbd', 'label', 'map', 'object', 'q', 's', 'samp', 'script',
    'select', 'small', 'span', 'strike', 'strong', 'sub', 'sup', 'textarea',
    'tt', 'u', 'var'];
  final List _closeSelfTags = const ['colgroup', 'dd', 'dt', 'li', 'options', 'p', 'td', 'tfoot', 'th', 'thead', 'tr'];
  final List _fillAttrs     = const ['checked', 'compact', 'declare', 'defer', 'disabled', 'ismap', 'multiple',
    'nohref', 'noresize', 'noshade', 'nowrap', 'readonly', 'selected'];
  final List _specialTags   = const ['script', 'style'];

  List _stack  = [];
  List _result = [];

  final TextStyle textStyle;

  Map<String, dynamic>? _tag;

  // =================================================================================================================

  _HtmlParser({ required this.textStyle }) {}

  // =================================================================================================================

  List parse(String html) {
    String last = html;
    Match? match;
    int index;
    bool chars;

    while (html.length > 0) {
      chars = true;

      // Make sure we're not in a script or style element
      if (this._getStackLastItem() == null || !this._specialTags.contains(this._getStackLastItem())) {
        // Comment
        if (html.indexOf('<!--') == 0) {
          index = html.indexOf('-->');

          if (index >= 0) {
            html = html.substring(index + 3);
            chars = false;
          }
        }
        // End tag
        else if (html.indexOf('</') == 0) {
          match = this._endTag.firstMatch(html);

          if (match != null) {
            String tag = match[0]!;

            html  = html.substring(tag.length);
            chars = false;

            this._parseEndTag(tag);
          }
        }
        // Start tag
        else if (html.indexOf('<') == 0) {
          match = this._startTag.firstMatch(html);

          if (match != null) {
            String tag = match[0]!;

            html  = html.substring(tag.length);
            chars = false;

            this._parseStartTag(tag, match[1]!, match[2]!, match.start);
          }
        }

        if (chars) {
          index = html.indexOf('<');

          String text = (index < 0) ? html : html.substring(0, index);

          html = (index < 0) ? '' : html.substring(index);

          this._appendNode(text);
        }
      } else {
        RegExp re = RegExp(r'(.*)<\/' + (this._getStackLastItem() ?? "") + r'[^>]*>');

        html = html.replaceAllMapped(re, (Match match) {
          String text = (match[0] ?? "")
            ..replaceAll(RegExp('<!--(.*?)-->'), '\$1')
            ..replaceAll(RegExp('<!\[CDATA\[(.*?)]]>'), '\$1');

          this._appendNode(text);

          return '';
        });

        this._parseEndTag(this._getStackLastItem());
      }

      if (html == last) {
        throw 'Parse Error: ' + html;
      }

      last = html;
    }

    // Cleanup any remaining tags
    this._parseEndTag();

    List result = this._result;

    // Cleanup internal variables
    this._stack  = [];
    this._result = [];

    return result;
  }

  // =================================================================================================================

  void _parseStartTag(String tag, String tagName, String rest, int unary) {
    tagName = tagName.toLowerCase();

    if (this._blockTags.contains(tagName)) {
      while (this._getStackLastItem() != null && this._inlineTags.contains(this._getStackLastItem())) {
        this._parseEndTag(this._getStackLastItem());
      }
    }

    if (this._closeSelfTags.contains(tagName) && this._getStackLastItem() == tagName) {
      this._parseEndTag(tagName);
    }

    if (this._emptyTags.contains(tagName)) {
      unary = 1;
    }

    if (unary == 0) {
      this._stack.add(tagName);
    }

    Map attrs = {};

    Iterable<Match> matches = this._attr.allMatches(rest);

    for (Match match in matches) {
      String? attribute = match[1];
      if (attribute != null) {
        String value = "";

        if (match[2] != null) {
          value = match[2]!;
        } else if (match[3] != null) {
          value = match[3]!;
        } else if (match[4] != null) {
          value = match[4]!;
        } else if (this._fillAttrs.contains(attribute)) {
          value = attribute;
        }

        attrs[attribute] = value;
      }
    }

    this._appendTag(tagName, attrs);
  }

  // =================================================================================================================

  void _parseEndTag([String? tagName]) {
    int pos;

    // If no tag name is provided, clean shop
    if (tagName == null) {
      pos = 0;
    }
    // Find the closest opened tag of the same type
    else {
      for (pos = this._stack.length - 1; pos >= 0; pos--) {
        if (this._stack[pos] == tagName) {
          break;
        }
      }
    }

    if (pos >= 0) {
      // Remove the open elements from the stack
      this._stack.removeRange(pos, this._stack.length);
    }
  }

  // =================================================================================================================

  TextStyle _parseStyle(String tag, Map attrs) {
    Iterable<Match> matches;
    String? style = attrs['style'];
    String param;
    String value;

    Color color                   = this.textStyle.color ?? Colors.black;
    FontWeight fontWeight         = this.textStyle.fontWeight ?? FontWeight.normal;
    FontStyle fontStyle           = this.textStyle.fontStyle ?? FontStyle.normal;
    TextDecoration textDecoration = this.textStyle.decoration ?? TextDecoration.none;

    switch (tag) {
      case 'a':
        color = Color(int.parse('0xFF1965B5'));
        break;

      case 'b':
      case 'strong':
        fontWeight = FontWeight.bold;
        break;

      case 'i':
      case 'em':
        fontStyle = FontStyle.italic;
        break;

      case 'u':
        textDecoration = TextDecoration.underline;
        break;
    }

    if (style != null) {
      matches = this._style.allMatches(style);

      for (Match match in matches) {
        param = (match[1] ?? "").trim();
        value = (match[2] ?? "").trim();

        switch (param) {
          case 'color':
            if (this._color.hasMatch(value)) {
              value = value.replaceAll('#', '').trim();
              color = Color(int.parse('0xFF' + value));
            }
            break;

          case 'font-weight':
            fontWeight = (value == 'bold') ? FontWeight.bold : FontWeight.normal;
            break;

          case 'font-style':
            fontStyle = (value == 'italic') ? FontStyle.italic : FontStyle.normal;
            break;

          case 'text-decoration':
            textDecoration = (value == 'underline') ? TextDecoration.underline : TextDecoration.none;
            break;
        }
      }
    }

    return this.textStyle.copyWith(
        color: color,
        fontWeight: fontWeight,
        fontStyle: fontStyle,
        decoration: textDecoration
    );
  }

  // =================================================================================================================

  void _appendTag(String tag, Map attrs) {
    this._tag = {
      'tag':   tag,
      'attrs': attrs
    };
  }

  // =================================================================================================================

  void _appendNode(String text) {
    if (this._tag == null) {
      this._tag = {
        'tag':   'p',
        'attrs': {}
      };
    }

    this._tag!['text']  = text;
    this._tag!['style'] = this._parseStyle(this._tag!['tag'], this._tag!['attrs']);
    this._tag!['href']  = (this._tag!['attrs']['href'] != null) ? this._tag!['attrs']['href'] : '';

    this._tag!.remove('attrs');

    this._result.add(this._tag);

    this._tag = null;
  }

  // =================================================================================================================

  String? _getStackLastItem() {
    if (this._stack.length <= 0) {
      return null;
    }

    return this._stack[this._stack.length - 1];
  }
}