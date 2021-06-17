import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:assets_audio_player/assets_audio_player.dart';
import 'package:dhamma_app/common/appConstants.dart';
import 'package:dhamma_app/data/dhammaDataParser.dart';
import 'package:dhamma_app/widgets/html_text.dart';
import 'package:dhamma_app/widgets/audioPlayer.dart';

class DetailContent extends StatelessWidget {

  final DataEntry? entry;
  final bool noTitle;

  DetailContent(this.entry, { this.noTitle = false }) : super();

  @override
  Widget build(BuildContext context) {
    if (entry == null) {
      return Container();

    } else {
      final Widget title = Text(
        entry!.title,
        textAlign: TextAlign.center,
        style: TextStyle(
          fontFamily: AppConstants.fontFamilyUnicode,
          fontSize: 16,
          fontWeight: FontWeight.bold,
        ),
      );

      final Widget detail = Padding(
        padding: EdgeInsets.only(bottom: 16.0),
        child: HtmlText(
          data: entry!.body,
          textStyle: TextStyle(
            fontFamily: AppConstants.fontFamilyUnicode,
          ),
        ),
      );

      List<Widget> children = [];
      if (!noTitle) {
        children.add(title);
      }
      children.add(detail);

      if (entry!.descriptionTitle != "") {
        children.add(Divider());
        children.add(Text(
          entry!.descriptionTitle,
          style: TextStyle(
            fontFamily: AppConstants.fontFamilyUnicode,
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ));

        children.add(Padding(
          padding: EdgeInsets.only(top: 8.0, left: 8.0, right: 8.0),
          child: Text(
            entry!.descriptionBody,
            style: TextStyle(
              fontFamily: AppConstants.fontFamilyUnicode,
            ),
          ),
        ));
      }

      if (entry!.soundFile != "") {
        final source = AudioSource(
            audio: Audio('assets/audios/${entry!.soundFile}'),
            name: entry!.title
        );

        if (!kIsWeb && !Platform.isLinux && !Platform.isMacOS && !Platform.isWindows) {
          children.add(Divider());
          children.add(Padding(
            padding: EdgeInsets.only(top: 8.0),
            child: AudioPlayer(audioSource: source),
          ));
        }
      }

      return SingleChildScrollView(
        scrollDirection: Axis.vertical,
        child: Container(
          alignment: Alignment.topCenter,
          padding: EdgeInsets.symmetric(vertical: 16.0, horizontal: 8.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: children,
          ),
        ),
      );
    }
  }

}