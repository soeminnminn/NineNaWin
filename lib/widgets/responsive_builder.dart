import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

enum DeviceScreenType {
  mobile,
  tablet,
  desktop,
  watch
}

enum RefinedSize { small, normal, large, extraLarge }

/// Contains sizing information to make responsive choices for the current screen
class SizingInformation {
  final DeviceScreenType deviceScreenType;
  final RefinedSize refinedSize;
  final Size screenSize;
  final Size localWidgetSize;

  bool get isMobile => deviceScreenType == DeviceScreenType.mobile;

  bool get isTablet => deviceScreenType == DeviceScreenType.tablet;

  bool get isDesktop => deviceScreenType == DeviceScreenType.desktop;

  bool get isWatch => deviceScreenType == DeviceScreenType.watch;

  // Refined

  bool get isExtraLarge => refinedSize == RefinedSize.extraLarge;

  bool get isLarge => refinedSize == RefinedSize.large;

  bool get isNormal => refinedSize == RefinedSize.normal;

  bool get isSmall => refinedSize == RefinedSize.small;

  SizingInformation({
    required this.deviceScreenType,
    required this.refinedSize,
    required this.screenSize,
    required this.localWidgetSize,
  });

  @override
  String toString() {
    return 'DeviceType:$deviceScreenType RefinedSize:$refinedSize ScreenSize:$screenSize LocalWidgetSize:$localWidgetSize';
  }
}

/// Manually define screen resolution breakpoints
///
/// Overrides the defaults
class ScreenBreakpoints {
  final double watch;
  final double tablet;
  final double desktop;

  const ScreenBreakpoints({
    required this.desktop,
    required this.tablet,
    required this.watch,
  });

  @override
  String toString() {
    return "Desktop: $desktop, Tablet: $tablet, Watch: $watch";
  }

  static const breakPoints = const ScreenBreakpoints(
    desktop: 950,
    tablet: 600,
    watch: 300,
  );
}

/// Manually define refined breakpoints
///
/// Overrides the defaults
class RefinedBreakpoints {
  final double mobileSmall;
  final double mobileNormal;
  final double mobileLarge;
  final double mobileExtraLarge;

  final double tabletSmall;
  final double tabletNormal;
  final double tabletLarge;
  final double tabletExtraLarge;

  final double desktopSmall;
  final double desktopNormal;
  final double desktopLarge;
  final double desktopExtraLarge;

  const RefinedBreakpoints({
    this.mobileSmall = 320,
    this.mobileNormal = 375,
    this.mobileLarge = 414,
    this.mobileExtraLarge = 480,
    this.tabletSmall = 600,
    this.tabletNormal = 768,
    this.tabletLarge = 850,
    this.tabletExtraLarge = 900,
    this.desktopSmall = 950,
    this.desktopNormal = 1920,
    this.desktopLarge = 3840,
    this.desktopExtraLarge = 4096,
  });

  @override
  String toString() {
    return "Desktop: Small - $desktopSmall Normal - $desktopNormal Large - $desktopLarge ExtraLarge - $desktopExtraLarge" +
        "\nTablet: Small - $tabletSmall Normal - $tabletNormal Large - $tabletLarge ExtraLarge - $tabletExtraLarge" +
        "\nMobile: Small - $mobileSmall Normal - $mobileNormal Large - $mobileLarge ExtraLarge - $mobileExtraLarge";
  }

  static const breakPoints = const RefinedBreakpoints(
    // Desktop
    desktopExtraLarge: 4096,
    desktopLarge: 3840,
    desktopNormal: 1920,
    desktopSmall: 950,
    // Tablet
    tabletExtraLarge: 900,
    tabletLarge: 850,
    tabletNormal: 768,
    tabletSmall: 600,
    // Mobile
    mobileExtraLarge: 480,
    mobileLarge: 414,
    mobileNormal: 375,
    mobileSmall: 320,
  );
}

/// Returns the [DeviceScreenType] that the application is currently running on
DeviceScreenType _getDeviceType(
    Size size, [
      ScreenBreakpoints? breakpoint,
    ]) {
  double deviceWidth = size.shortestSide;

  if (kIsWeb || Platform.isLinux || Platform.isMacOS || Platform.isWindows) {
    deviceWidth = size.width;
  }

  // Replaces the defaults with the user defined definitions
  if (breakpoint != null) {
    if (deviceWidth > breakpoint.desktop) {
      return DeviceScreenType.desktop;
    }

    if (deviceWidth > breakpoint.tablet) {
      return DeviceScreenType.tablet;
    }

    if (deviceWidth < breakpoint.watch) {
      return DeviceScreenType.watch;
    }
  } else {
    // If no user defined definitions are passed through use the defaults
    if (deviceWidth >= ScreenBreakpoints.breakPoints.desktop) {
      return DeviceScreenType.desktop;
    }

    if (deviceWidth >= ScreenBreakpoints.breakPoints.tablet) {
      return DeviceScreenType.tablet;
    }

    if (deviceWidth < ScreenBreakpoints.breakPoints.watch) {
      return DeviceScreenType.watch;
    }
  }

  return DeviceScreenType.mobile;
}

/// Returns the [RefindedSize] for each device that the application is currently running on
RefinedSize _getRefinedSize(
    Size size, {
      RefinedBreakpoints? refinedBreakpoint,
      bool isWebOrDesktop = kIsWeb,
    }) {
  DeviceScreenType deviceScreenType = _getDeviceType(size);
  double deviceWidth = size.shortestSide;

  if (isWebOrDesktop) {
    deviceWidth = size.width;
  }

  // Replaces the defaults with the user defined definitions
  if (refinedBreakpoint != null) {
    if (deviceScreenType == DeviceScreenType.desktop) {
      if (deviceWidth > refinedBreakpoint.desktopExtraLarge) {
        return RefinedSize.extraLarge;
      }

      if (deviceWidth > refinedBreakpoint.desktopLarge) {
        return RefinedSize.large;
      }

      if (deviceWidth > refinedBreakpoint.desktopNormal) {
        return RefinedSize.normal;
      }
    }

    if (deviceScreenType == DeviceScreenType.tablet) {
      if (deviceWidth > refinedBreakpoint.tabletExtraLarge) {
        return RefinedSize.extraLarge;
      }

      if (deviceWidth > refinedBreakpoint.tabletLarge) {
        return RefinedSize.large;
      }

      if (deviceWidth > refinedBreakpoint.tabletNormal) {
        return RefinedSize.normal;
      }
    }

    if (deviceScreenType == DeviceScreenType.mobile) {
      if (deviceWidth > refinedBreakpoint.mobileExtraLarge) {
        return RefinedSize.extraLarge;
      }

      if (deviceWidth > refinedBreakpoint.mobileLarge) {
        return RefinedSize.large;
      }

      if (deviceWidth > refinedBreakpoint.mobileNormal) {
        return RefinedSize.normal;
      }
    }

    if (deviceScreenType == DeviceScreenType.watch) {
      return RefinedSize.normal;
    }
  } else {
    // If no user defined definitions are passed through use the defaults

    // Desktop
    if (deviceScreenType == DeviceScreenType.desktop) {
      if (deviceWidth >= RefinedBreakpoints.breakPoints.desktopExtraLarge) {
        return RefinedSize.extraLarge;
      }

      if (deviceWidth >= RefinedBreakpoints.breakPoints.desktopLarge) {
        return RefinedSize.large;
      }

      if (deviceWidth >= RefinedBreakpoints.breakPoints.desktopNormal) {
        return RefinedSize.normal;
      }
    }

    // Tablet
    if (deviceScreenType == DeviceScreenType.tablet) {
      if (deviceWidth >= RefinedBreakpoints.breakPoints.tabletExtraLarge) {
        return RefinedSize.extraLarge;
      }

      if (deviceWidth >= RefinedBreakpoints.breakPoints.tabletLarge) {
        return RefinedSize.large;
      }

      if (deviceWidth >= RefinedBreakpoints.breakPoints.tabletNormal) {
        return RefinedSize.normal;
      }
    }

    // Mobile
    if (deviceScreenType == DeviceScreenType.mobile) {
      if (deviceWidth >= RefinedBreakpoints.breakPoints.mobileExtraLarge) {
        return RefinedSize.extraLarge;
      }

      if (deviceWidth >= RefinedBreakpoints.breakPoints.mobileLarge) {
        return RefinedSize.large;
      }

      if (deviceWidth >= RefinedBreakpoints.breakPoints.mobileNormal) {
        return RefinedSize.normal;
      }
    }
  }

  return RefinedSize.small;
}

/// A widget with a builder that provides you with the sizingInformation
///
/// This widget is used by the ScreenTypeLayout to provide different widget builders
class ResponsiveBuilder extends StatelessWidget {
  final Widget Function(
      BuildContext context,
      SizingInformation sizingInformation,
      ) builder;

  final ScreenBreakpoints? breakpoints;
  final RefinedBreakpoints? refinedBreakpoints;

  const ResponsiveBuilder({
    Key? key,
    required this.builder,
    this.breakpoints,
    this.refinedBreakpoints,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(builder: (context, boxConstraints) {
      var mediaQuery = MediaQuery.of(context);
      var sizingInformation = SizingInformation(
        deviceScreenType: _getDeviceType(mediaQuery.size, breakpoints),
        refinedSize: _getRefinedSize(
          mediaQuery.size,
          refinedBreakpoint: refinedBreakpoints,
        ),
        screenSize: mediaQuery.size,
        localWidgetSize:
        Size(boxConstraints.maxWidth, boxConstraints.maxHeight),
      );
      return builder(context, sizingInformation);
    });
  }
}