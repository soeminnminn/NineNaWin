import 'package:flutter/material.dart';

import 'common/appConstants.dart';
import 'pages/mainPage.dart';

void main() {
  runApp(MainApp());
}

class MainApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: AppConstants.appTitle,
      debugShowCheckedModeBanner: false,
      theme: ThemeData.light().copyWith(
        primaryColor: Color(AppConstants.colorPrimary),
        accentColor: Color(AppConstants.colorAccent),
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      darkTheme: ThemeData.dark().copyWith(
        primaryColor: Color(AppConstants.colorPrimary),
        accentColor: Color(AppConstants.colorAccent),
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MainPage(title: AppConstants.appTitle),
    );
  }
}


