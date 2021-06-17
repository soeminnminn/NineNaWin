import 'package:flutter/material.dart';

import 'package:dhamma_app/common/appConstants.dart';
import 'package:dhamma_app/data/dhammaDataParser.dart';
import 'package:dhamma_app/widgets/detailContent.dart';

class DetailsPage extends StatefulWidget {

  final DataEntry dataEntry;

  DetailsPage(this.dataEntry): super();

  @override
  State<StatefulWidget> createState() => _DetailsPageState();
}

class _DetailsPageState extends State<DetailsPage> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.dataEntry.title,
          style: TextStyle(
            fontFamily: AppConstants.fontFamilyUnicode,
          ),
        ),
      ),
      body: SafeArea(
        child: DetailContent(
          widget.dataEntry,
          noTitle: true,
        ),
      ),
    );
  }

}