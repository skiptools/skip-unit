// swift-tools-version: 5.8
import PackageDescription

let package = Package(
    name: "skip-unit",
    defaultLocalization: "en",
    platforms: [.iOS(.v16), .macOS(.v13), .tvOS(.v16), .watchOS(.v9), .macCatalyst(.v16)],
    products: [
        .library(name: "SkipUnit", targets: ["SkipUnit"]),
        .library(name: "SkipUnitKt", targets: ["SkipUnitKt"]),
    ],
    dependencies: [
        .package(url: "https://source.skip.tools/skip.git", from: "0.6.11"),
    ],
    targets: [
        .target(name: "SkipUnit", dependencies: [.product(name: "SkipDrive", package: "skip")]),
        .target(name: "SkipUnitKt", resources: [.process("Skip")], plugins: [.plugin(name: "transpile", package: "skip")]),
        .testTarget(name: "SkipUnitTests", dependencies: ["SkipUnit"]),
        .testTarget(name: "SkipUnitKtTests", dependencies: ["SkipUnitKt", "SkipUnit"], resources: [.process("Skip")], plugins: [.plugin(name: "transpile", package: "skip")]),
    ]
)
