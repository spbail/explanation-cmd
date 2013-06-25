# OWL Explanation command line tool

A wrapper for the OWL explanation library by Matthew Horridge.

## Usage

    java -jar explanation-cmd.jar -o owlfile -e entailmentfile [options]

Options

    -t timeout in seconds (default: no timeout)

    -l maximum number of justifications (default: no limit)

    -d output directory (default: print to stdout)

## Examples
Use files in the "test" directory:

    java -jar explanation-cmd.jar -o biopax.owl.xml -e entailment2.txt -l 10 -t 30

## Please note
* If no output directory is given, the explanations are printed to stdout.
* Timeouts may not work. (Long story...)
* Default reasoner at the moment is Pellet, but feel free to drop any other OWL 2 reasoner that supports entailment checking into "lib" and uncomment (or add) the respective lines in Util.
