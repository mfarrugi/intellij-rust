#[macro_use]
extern crate log;

use std::collections::HashMap;

mod Stuff;

pub enum <ENUM>Flag</ENUM> {
    Good,
    Bad,
    Ugly
}

pub trait <TRAIT>Write</TRAIT> {
    fn write(&mut self, buf: &[u8]) -> Result<usize>;
}

struct <STRUCT>Object</STRUCT><T> {
    flag: Flag,
    fields: HashMap<T, u64>
}

/* Block comment */
fn <function-decl>main</function-decl>() {
    // A simple integer calculator:
    // `+` or `-` means add or subtract by 1
    // `*` or `/` means multiply or divide by 2

    let input = Option::None;
    let program = input.unwrap_or_else(|| "+ + * - /");
    let mut <mut-binding>accumulator</mut-binding> = 0;

    for token in program.chars() {
        match token {
            '+' => <mut-binding>accumulator</mut-binding> += 1,
            '-' => <mut-binding>accumulator</mut-binding> -= 1,
            '*' => <mut-binding>accumulator</mut-binding> *= 2,
            '/' => <mut-binding>accumulator</mut-binding> /= 2,
            _ => { /* ignore everything else */ }
        }
    }

    <macro>info!</macro>("The program \"{}\" calculates the value {}",
             program, <mut-binding>accumulator</mut-binding>);
}

/// Some documentation
<attribute>#[cfg(target_os=</attribute>"linux"<attribute>)]</attribute>
unsafe fn <function-decl>a_function</function-decl><<type-parameter>T</type-parameter>: 'lifetime>() {
    'label: loop {
        println!("Hello\x20W\u{f3}rld!\u{abcdef}");
    }
}
