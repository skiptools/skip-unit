// Copyright 2023–2026 Skip
// SPDX-License-Identifier: MPL-2.0
import Testing

@Test func freestandingTests() {
    #expect("swift" != "kotlin")
}

@Suite class TestingTests {
    @Test func testTestingTests() {
        #expect(1 == 1)
        #expect(1 != 1 + 1)
        #expect(1 < 2)
        #expect(2 <= 2)
        #expect(2 >= 2)
        #expect(3 > 2)
    }
}
