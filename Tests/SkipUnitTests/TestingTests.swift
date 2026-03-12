// Copyright 2023–2026 Skip
// SPDX-License-Identifier: LGPL-3.0-only WITH LGPL-3.0-linking-exception
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
