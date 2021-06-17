import 'package:flutter/services.dart' show rootBundle;
import 'package:xml/xml.dart' as xml;

extension IterableXmlElements on Iterable<xml.XmlElement> {
  xml.XmlElement? firstOrNull() {
    if (this.length == 0) return null;
    return this.first;
  }

  String? textOfFirst() {
    if (this.length == 0) return null;
    return this.first.text;
  }
}

class DataCategory {
  String name = "";
  String title = "";
  List<int> entries = [];
}

class DataEntry {
  String categoryName = "";

  String name = "";
  DataCategory? category;
  String title = "";
  String soundUrl = "";
  String soundFile = "";
  String body = "";

  String descriptionTitle = "";
  String descriptionBody = "";
}

class DhammaDataParser {

  List<DataCategory> categories = [];
  List<DataEntry> entries = [];

  Future<DhammaDataParser> parse() async {
    String xmlData = await rootBundle.loadString('assets/data.xml');
    xml.XmlDocument document = xml.XmlDocument.parse(xmlData);

    xml.XmlElement root = document.rootElement;

    xml.XmlElement? elmCategories = root.findElements('categories').firstOrNull();
    if (elmCategories != null) {
      _parseCategories(elmCategories.findAllElements('category').toList());

      _parseEntries(root.findAllElements('entry').toList());
    }

    return this;
  }

  void _parseCategories(List<xml.XmlElement> elm) {
    elm.forEach((element) {
      DataCategory item = new DataCategory();
      item.name = element.getAttribute('name') ?? "";
      item.title = element.text.trim();
      categories.add(item);
    });
  }

  void _parseEntries(List<xml.XmlElement> elm) {
    elm.forEach((element) {
      String? category = element.getAttribute('category');
      int index = entries.length;

      DataEntry item = new DataEntry();
      item.categoryName = category ?? "";
      item.name = "entry_$index";
      item.title = element.findElements('title').textOfFirst()  ?? "";
      item.body = element.findElements('body').textOfFirst() ?? "";

      xml.XmlElement? elmSound = element.findElements('sound').firstOrNull();
      item.soundUrl = elmSound!.findElements('url').textOfFirst() ?? "";
      item.soundFile = elmSound.findElements('fileName').textOfFirst() ?? "";

      xml.XmlElement? elmDesc = element.findElements('description').firstOrNull();
      if (elmDesc != null) {
        item.descriptionTitle = elmDesc.findElements('title').textOfFirst() ?? "";
        item.descriptionBody = elmDesc.findElements('body').textOfFirst() ?? "";
      }

      int categoryIndex = _findCategory(category);
      if (categoryIndex > -1) {
        categories[categoryIndex].entries.add(index);
        item.category = categories[categoryIndex];
      }

      entries.add(item);
    });
  }

  int _findCategory(String? name) {
    if (name == null) return -1;
    DataCategory found = categories.firstWhere((element) => element.name == name);
    return categories.indexOf(found);
  }

  String getCategory(String name) {
    int categoryIndex = _findCategory(name);
    if (categoryIndex > -1) {
      return categories[categoryIndex].title;
    }
    return "";
  }
}