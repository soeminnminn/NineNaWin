import 'package:flutter/material.dart';
import 'package:flutter_neumorphic/flutter_neumorphic.dart';
import 'package:assets_audio_player/assets_audio_player.dart';

class AudioSource {
  final Audio audio;
  final String name;

  const AudioSource({
    required this.audio,
    required this.name
  });
}

String _durationToString(Duration duration) {
  String twoDigits(int n) {
    if (n >= 10) return '$n';
    return '0$n';
  }

  final twoDigitMinutes =
  twoDigits(duration.inMinutes.remainder(Duration.minutesPerHour));
  final twoDigitSeconds =
  twoDigits(duration.inSeconds.remainder(Duration.secondsPerMinute));
  return '$twoDigitMinutes:$twoDigitSeconds';
}

class _PositionSeek extends StatefulWidget {
  final Duration currentPosition;
  final Duration duration;
  final Function(Duration) seekTo;

  const _PositionSeek({
    required this.currentPosition,
    required this.duration,
    required this.seekTo,
  });

  @override
  _PositionSeekState createState() => _PositionSeekState();
}

class _PositionSeekState extends State<_PositionSeek> {
  late Duration _visibleValue;
  bool listenOnlyUserInteraction = false;
  double get percent => widget.duration.inMilliseconds == 0
      ? 0
      : _visibleValue.inMilliseconds / widget.duration.inMilliseconds;

  @override
  void initState() {
    super.initState();
    _visibleValue = widget.currentPosition;
  }

  @override
  void didUpdateWidget(_PositionSeek oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (!listenOnlyUserInteraction) {
      _visibleValue = widget.currentPosition;
    }
  }

  @override
  Widget build(BuildContext context) {
    final accentColor = NeumorphicTheme.currentTheme(context).accentColor;
    final depth = NeumorphicTheme.depth(context) ?? 4;

    return Padding(
      padding: const EdgeInsets.only(left: 16.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          Expanded(
            child: NeumorphicSlider(
              height: 14,
              min: 0,
              max: widget.duration.inMilliseconds.toDouble(),
              value: percent * widget.duration.inMilliseconds.toDouble(),
              style: SliderStyle(
                variant: accentColor,
                depth: depth,
              ),
              onChangeEnd: (newValue) {
                setState(() {
                  listenOnlyUserInteraction = false;
                  widget.seekTo(_visibleValue);
                });
              },
              onChangeStart: (_) {
                setState(() {
                  listenOnlyUserInteraction = true;
                });
              },
              onChanged: (newValue) {
                setState(() {
                  final to = Duration(milliseconds: newValue.floor());
                  _visibleValue = to;
                });
              },
            ),
          ),
          SizedBox(
            height: 14,
            width: 40,
            child: Text(_durationToString(widget.currentPosition)),
          ),
          // SizedBox(
          //   height: 14,
          //   width: 40,
          //   child: Text(_durationToString(widget.duration)),
          // ),
        ],
      ),
    );
  }
}

class AudioPlayer extends StatefulWidget {

  final AudioSource audioSource;

  const AudioPlayer({
    required this.audioSource
  });

  @override
  State<StatefulWidget> createState() => _AudioPlayerState();
}

class _AudioPlayerState extends State<AudioPlayer> {
  final AssetsAudioPlayer _assetsAudioPlayer = AssetsAudioPlayer.newPlayer();

  @override
  void initState() {
    super.initState();
    _assetsAudioPlayer.open(
        widget.audioSource.audio,
        autoStart: false,
        showNotification: true);
  }

  @override
  void dispose() {
    _assetsAudioPlayer.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final accentColor = Theme.of(context).accentColor;
    return NeumorphicTheme(
      themeMode: ThemeMode.system, //or dark / system
      darkTheme: NeumorphicThemeData(
        baseColor: Color(0xff333333),
        accentColor: accentColor,
        variantColor: Colors.white,
        lightSource: LightSource.topLeft,
        depth: 4,
        intensity: 0.5,
      ),
      theme: NeumorphicThemeData(
        baseColor: Color(0xffDDDDDD),
        accentColor: accentColor,
        variantColor: Colors.grey[700] ?? Colors.grey,
        lightSource: LightSource.topLeft,
        depth: 6,
        intensity: 0.8,
      ),
      child: _buildPlayer(context),
    );
  }

  Widget _buildPlayer(BuildContext context) {
    final accentColor = Theme.of(context).accentColor;
    return PlayerBuilder.isPlaying(
      player: _assetsAudioPlayer,
      builder: (context, isPlaying) => Neumorphic(
        margin: EdgeInsets.all(8),
        style: NeumorphicStyle(
          boxShape: NeumorphicBoxShape.roundRect(BorderRadius.circular(8)),
        ),
        padding: const EdgeInsets.all(12.0),
        child: Row(
          children: <Widget>[
            NeumorphicButton(
              style: NeumorphicStyle(
                boxShape: NeumorphicBoxShape.circle(),
              ),
              padding: EdgeInsets.all(16),
              onPressed: () {
                _assetsAudioPlayer.playOrPause();
              },
              child: Icon(
                isPlaying
                    ? Icons.pause
                    : Icons.play_arrow,
                size: 32,
                color: accentColor,
              ),
            ),
            Expanded(
              child: PlayerBuilder.realtimePlayingInfos(
                player: _assetsAudioPlayer,
                builder: (context, RealtimePlayingInfos infos) => _PositionSeek(
                  seekTo: (to) {
                    _assetsAudioPlayer.seek(to);
                  },
                  duration: infos.duration,
                  currentPosition: infos.currentPosition,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

}