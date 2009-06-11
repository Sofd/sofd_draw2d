default: statechart.pdf

%.eps: %.dia
	dia -n -e $@ -t eps $<

%.pdf: %.eps
	epstopdf --outfile=$@ $<
