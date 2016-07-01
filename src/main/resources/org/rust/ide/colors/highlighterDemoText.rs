<ATTRIBUTE>#[macro_use]</ATTRIBUTE>
extern crate log;

use std::collections::<STRUCT>HashMap</STRUCT>;

mod <MODULE>Stuff</MODULE>;

pub enum <ENUM>Flag</ENUM> {
    Good,
    Bad,
    Ugly
}

pub trait <TRAIT>Write</TRAIT> {
    fn <INSTANCE_METHOD>write</INSTANCE_METHOD>(&mut self, buf: &[u8]) -> <ENUM>Result</ENUM><usize>;
}

struct <STRUCT>Object</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>> {
    flag: <ENUM>Flag</ENUM>,
    fields: <STRUCT>HashMap</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>, u64>
}

/* Block comment */
fn <FUNCTION_DECLARATION>main</FUNCTION_DECLARATION>() {
    // A simple integer calculator:
    // `+` or `-` means add or subtract by 1
    // `*` or `/` means multiply or divide by 2

    let input = <ENUM>Option</ENUM>::None;
    let program = input.<INSTANCE_METHOD>unwrap_or_else</INSTANCE_METHOD>(|| "+ + * - /");
    let mut <MUT_BINDING>accumulator</MUT_BINDING> = 0;

    for token in program.<INSTANCE_METHOD>chars</INSTANCE_METHOD>() {
        match token {
            '+' => <MUT_BINDING>accumulator</MUT_BINDING> += 1,
            '-' => <MUT_BINDING>accumulator</MUT_BINDING> -= 1,
            '*' => <MUT_BINDING>accumulator</MUT_BINDING> *= 2,
            '/' => <MUT_BINDING>accumulator</MUT_BINDING> /= 2,
            _ => { /* ignore everything else */ }
        }
    }

    <MACRO>info!</MACRO>("The program \"{}\" calculates the value {}",
             program, <MUT_BINDING>accumulator</MUT_BINDING>);
}

/// Some documentation
<ATTRIBUTE>#[cfg(target_os=</ATTRIBUTE>"linux"<ATTRIBUTE>)]</ATTRIBUTE>
unsafe fn <FUNCTION_DECLARATION>a_function</FUNCTION_DECLARATION><<TYPE_PARAMETER>T</TYPE_PARAMETER>: <LIFETIME>'lifetime</LIFETIME>>() {
    'label: loop {
        <MACRO>println!</MACRO>("Hello\x20W\u{f3}rld!\u{abcdef}");
    }
}
