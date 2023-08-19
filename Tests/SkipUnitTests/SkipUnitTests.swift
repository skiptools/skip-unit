// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org
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
