{:layout :post
 :title "Another forgotten binding.pry"
 :date "2013-08-12"
 :category :code
 :tags [:code :rails :git :debugger :pry :diff]}

It's a common occurrence, or a least it was. Tools like [pry][] and [debugger][]
are almost indispensable once you have gotten used to them, however sometimes
they get forgotten.

If you are a very careful person you might proof read your diff before
committing your code. Good for you! Cookies await you in your kitchen although
you might have to make them first. Anyways, even if you are very careful chances
are you'll still miss stuff. Sometimes that stuff is a binding.pry in a Rails
controller. You'll probably catch this before you deploy.

If you don't though&hellip;

Well, let's just avoid it in the first place.

    #!/bin/sh

    COMMAND='git grep -n --cached'
    ARGS='-e debugger -e "binding\.pry" -- app config db spec vendor script lib Rakefile Guardfile Capfile'

    if eval "$COMMAND -q $ARGS" ; then
      echo "You have a binding.pry or a debugger in a bad place.\n"
        eval "$COMMAND $ARGS"
        exit 1
    fi

    ARGS='-e "^<<<<<<< " -e "^>>>>>>> " -e "^=======$" -- app config db spec vendor script lib Rakefile Guardfile Capfile'

    if eval "$COMMAND -q $ARGS" ; then
        echo "You have some left over diff artifacts.\n"
        eval "$COMMAND $ARGS"
        exit 1
    fi

[A gist of it](https://gist.github.com/RyanMcG/5775028#file-rails-pre-commit)

This is just a simple (probably suboptimal) shell script that when run at the
root of a Rails project will attempt to use `git grep` to find instances of
`debugger` or `binding.pry` in your source.

As an added bonus it also checks for diff artifacts like `<<<<<<<`, `>>>>>>>`
and `=======`. Just to ensure you do not try to commit a merge conflict or the
like.

As you may have noticed it exits 1 when it finds something (defaults to 0
otherwise). That means that you can make this a git pre-commit hook! Here's how:

    curl https://gist.github.com/RyanMcG/5775028/raw/rails-pre-commit -o pre-commit
    chmod +x pre-commit
    mv pre-commit path/to/your/rails/root/.git/hooks/

That's it!
