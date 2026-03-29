// Copyright 2023–2026 Skip
// SPDX-License-Identifier: MPL-2.0
// SKIP SYMBOLFILE

#if SKIP
public func XCTAssert(_ a: Bool) {
}
public func XCTAssertTrue(_ a: Bool, _ msg: String = "") {
}
public func XCTAssertFalse(_ a: Bool, _ msg: String = "") {
}
public func XCTAssertNil(_ a: Any?, _ msg: String = "") {
}
public func XCTAssertNotNil(_ a: Any?, _ msg: String = "") {
}
public func XCTAssertIdentical<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func XCTAssertNotIdentical<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func XCTAssertEqual<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func XCTAssertNotEqual<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func XCTAssertEqual(_ a: Double, _ b: Double, accuracy: Double, _ msg: String = "") {
}
public func XCTAssertEqual(_ a: Float, _ b: Float, accuracy: Float, _ msg: String = "") {
}
public func XCTAssertGreaterThan<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func XCTAssertGreaterThanOrEqual<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func XCTAssertLessThan<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func XCTAssertLessThanOrEqual<T>(_ a: T, _ b: T, _ msg: String = "") {
}
// MARK: Swift Testing assertion function stubs
// These are targets for transpiled #expect() and #require() macros.

public func expectTrue(_ a: Bool, _ msg: String = "") {
}
public func expectFalse(_ a: Bool, _ msg: String = "") {
}
public func expectEqual<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func expectNotEqual<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func expectGreaterThan<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func expectGreaterThanOrEqual<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func expectLessThan<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func expectLessThanOrEqual<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func expectNil(_ a: Any?, _ msg: String = "") {
}
public func expectNotNil(_ a: Any?, _ msg: String = "") {
}
public func expectThrows<T: Error>(throws: T.Type, _ block: () throws -> Void) {
}
public func requireTrue(_ a: Bool, _ msg: String = "") {
}
public func requireEqual<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func requireNotEqual<T>(_ a: T?, _ b: T?, _ msg: String = "") {
}
public func requireGreaterThan<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func requireGreaterThanOrEqual<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func requireLessThan<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func requireLessThanOrEqual<T>(_ a: T, _ b: T, _ msg: String = "") {
}
public func requireNotNil<T>(_ a: T?) -> T {
    return a!
}
public func requireThrows<T: Error>(throws: T.Type, _ block: () throws -> Void) {
}
#endif
