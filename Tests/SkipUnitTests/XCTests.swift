// Copyright 2023–2026 Skip
// SPDX-License-Identifier: MPL-2.0
import XCTest

class SkipUnitTests: XCTestCase {
    func testSkipUnit() {
        XCTAssertEqual(1, 1)
        XCTAssertNotEqual(1, 1 + 1)
        XCTAssertLessThan(1, 2)
        XCTAssertLessThanOrEqual(2, 2)
        XCTAssertGreaterThanOrEqual(2, 2)
        XCTAssertGreaterThan(3, 2)
    }
}
