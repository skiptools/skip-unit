// swift-tools-version: 5.8
import PackageDescription

let package = Package(
    name: "skip-unit",
    platforms: [.iOS(.v16), .macOS(.v13), .tvOS(.v16), .watchOS(.v9), .macCatalyst(.v16)],
    products: [
        .library(name: "SkipUnit", targets: ["SkipUnit"]),
    ],
    dependencies: [
        .package(url: "https://source.skip.tools/skip.git", from: "0.6.36"),
    ],
    targets: [
        .target(name: "SkipUnit", dependencies: [.product(name: "SkipDrive", package: "skip")], plugins: [.plugin(name: "skipstone", package: "skip")]),
        .testTarget(name: "SkipUnitTests", dependencies: ["SkipUnit"], plugins: [.plugin(name: "skipstone", package: "skip")]),
    ]
)
