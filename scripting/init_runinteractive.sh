cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

if $cygwin; then
    # TODO: make this windoze stoff work again too
    WS="C:\Dokumente und Einstellungen\TK\workspace"
    PROJBASE="$WS\de.sofd.iseerui"
else
    PROJBASE=".."
fi

BUNDLE_CP=`cat "$PROJBASE/META-INF/MANIFEST.MF" | grepre '(lib/.*?),?$' | sed "s/^/$PROJBASE\//g" |  tr '\n' ':'`

if $cygwin; then
    # TODO: make this windoze stoff work again too
    BUNDLE_CP="`echo $BUNDLE_CP | tr '/' '\\' | tr ':' ';'`"
    LOCAL_CP="$PROJBASE\bin"
    CP="$LOCAL_CP;$BUNDLE_CP"

    BSH_CP="$CP;$CYGWHOME_W\usr\local\bsh\bsh-2.0b4.jar"
    GROOVY_CP="$CP"
else
    LOCAL_CP="$PROJBASE/bin"
    CP="$LOCAL_CP:$BUNDLE_CP"

    BSH_CP="$CP:/usr/local/bsh/bsh.jar"
    GROOVY_CP="$CP"
fi


echo using classpath: $CP >&2
