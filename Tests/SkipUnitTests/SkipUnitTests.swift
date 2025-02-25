// Copyright 2023–2025 Skip
// SPDX-License-Identifier: LGPL-3.0-only WITH LGPL-3.0-linking-exception
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
