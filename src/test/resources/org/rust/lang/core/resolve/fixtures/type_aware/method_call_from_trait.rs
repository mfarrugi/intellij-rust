mod a {
    pub trait T {
        fn frobnicate(&self);
    }
}

mod b {
    use super::a::T;
    use super::S;

    impl T for S {
        fn frobnicate(&self) {}
    }
}

struct S;


fn main() {
    use a::T;

    let s = S;
    s.<caret>frobnicate();
}
