# Changelog

## [1.0.2] - 2026-11-25
### Fixed
- Fixed thousands separator formatting bug for numbers ≥100,000 (millions, billions).
- Separated raw computational string from formatted display string to prevent double-formatting and NaN errors.
- Added id-ID locale thousands separator (dots) every 3 digits from the right.
- Parser defensively cleanses separators from inputs to ensure uninterrupted evaluation.
- Added comprehensive unit tests for separator formatting, stripping, and large numbers.

## [1.0.1] - 2026-11-25
### Changed
- Refactored UI to pure black (OLED) in Dark Mode.
- Implemented authentic multi-layered Liquid Glass for number keypad buttons.
- Fixed theme switching bug across all screens.
- Removed ambient bubbles and internal container blobs for a clean minimalist aesthetic.

## [1.0.0] - 2026-11-23
### Added
- Initial release of Kalkulator with Liquid Glass design.
- Arbitrary precision engine using BigDecimal and recursive-descent parser.
- History and settings storage via Jetpack DataStore Preferences.
- CI/CD workflow for automated signed APK releases.
