LATEX = pdflatex
BIBTEX = bibtex
VIEWER = evince

TARGET = IOT_project_doc
SRC = $(TARGET).tex

PDF_OUT = ../$(TARGET).pdf

all: $(TARGET).pdf
	cp $(TARGET).pdf $(PDF_OUT)

$(TARGET).pdf: $(SRC)
	$(LATEX) -interaction=nonstopmode $(SRC)
	$(LATEX) -interaction=nonstopmode $(SRC)

clean:
	rm -f *.aux *.log *.out *.toc *.lot *.lof *.bbl *.blg *.dvi $(TARGET).pdf

distclean: clean
	rm -f $(PDF_OUT)

view: $(TARGET).pdf
	$(VIEWER) $(TARGET).pdf &

.PHONY: all clean distclean view
