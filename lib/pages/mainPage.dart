import 'package:flutter/material.dart';

import 'package:dhamma_app/widgets/responsive_builder.dart';
import 'package:dhamma_app/data/dhammaDataParser.dart';
import 'package:dhamma_app/widgets/listContent.dart';
import 'package:dhamma_app/widgets/detailContent.dart';
import 'package:dhamma_app/pages/detailsPage.dart';

class MainPage extends StatefulWidget {
  MainPage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  DataEntry? detailEntry;

  @override
  Widget build(BuildContext context) {
    final ThemeData themeData = Theme.of(context);
    final Color dividerColor = themeData.dividerColor;

    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: SafeArea(
        child: ResponsiveBuilder(
          builder: (context, sizingInformation) {
            if (sizingInformation.deviceScreenType == DeviceScreenType.desktop) {
              return Row(
                children: <Widget>[
                  Expanded(
                      flex: 3,
                      child: ListContent(
                        onTouched: (DataEntry entry) {
                          setState(() {
                            detailEntry = entry;
                          });
                        },
                      )
                  ),
                  VerticalDivider(width: 1.0, color: dividerColor),
                  Expanded(
                    flex: 4,
                    child: DetailContent(detailEntry),
                  ),
                ],
              );
            }

            if (sizingInformation.deviceScreenType == DeviceScreenType.tablet) {

            }

            if (sizingInformation.deviceScreenType == DeviceScreenType.watch) {
              return Center(
                child: Text("Not supported"),
              );
            }

            return ListContent(
              onTouched: (DataEntry entry) {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder:  (context) => DetailsPage(entry)
                  )
                );
              },
            );
          },
        ),
      ),
    );
  }
}