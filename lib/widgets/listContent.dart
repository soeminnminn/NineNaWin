import 'package:flutter/material.dart';

import 'package:dhamma_app/common/appConstants.dart';
import 'package:dhamma_app/data/dhammaDataParser.dart';
import 'package:grouped_list/grouped_list.dart';

typedef OnTouchedCallback = void Function(DataEntry entry);

class ListContent extends StatefulWidget {

  final DhammaDataParser data = DhammaDataParser();
  final OnTouchedCallback onTouched;

  ListContent({ required this.onTouched }) : super();

  @override
  _ListContentState createState() => _ListContentState();
}

class _ListContentState extends State<ListContent> {

  @override
  Widget build(BuildContext context) {
    return Container(
      child: FutureBuilder<DhammaDataParser>(
        future: widget.data.parse(),
        builder: (BuildContext context, AsyncSnapshot<DhammaDataParser> snapshot) {
          if (snapshot.hasData) {
            return GroupedListView<DataEntry, String>(
              elements: snapshot.data!.entries,
              groupBy: (element) => element.categoryName,
              groupHeaderBuilder: (DataEntry entry) => Padding(
                padding: const EdgeInsets.all(8.0),
                child: Text(
                  entry.category!.title,
                  style: TextStyle(
                    fontFamily: AppConstants.fontFamilyUnicode,
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              itemBuilder: (context, DataEntry element) => Card(
                elevation: 4.0,
                margin: new EdgeInsets.symmetric(horizontal: 10.0, vertical: 6.0),
                child: Container(
                  child: ListTile(
                    contentPadding:
                    EdgeInsets.symmetric(horizontal: 20.0, vertical: 10.0),
                    title: Text(
                      element.title,
                      style: TextStyle(
                        fontFamily: AppConstants.fontFamilyUnicode,
                      ),
                    ),
                    trailing: Icon(Icons.chevron_right),
                    onTap: () {
                      widget.onTouched(element);
                    },
                  ),
                ),
              ),
            );
          } else {
            return Center(
              child: CircularProgressIndicator(),
            );
          }
        },
      ),
    );
  }
}