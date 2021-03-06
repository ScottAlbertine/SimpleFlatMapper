package org.sfm.csv.parser;

import java.io.IOException;

public abstract class AbstractCsvCharConsumer extends CsvCharConsumer {
	protected static final int HAS_CONTENT = 8;
	protected static final int IN_QUOTE = 4;
	protected static final int IN_CR = 2;
	protected static final int QUOTE = 1;
	protected static final int NONE = 0;
	protected static final int TURN_OFF_IN_CR_MASK = ~IN_CR;
	protected static final int ALL_QUOTES = QUOTE | IN_QUOTE;
	protected final CharBuffer csvBuffer;

	protected int _currentIndex;
	protected int currentState = NONE;

	public AbstractCsvCharConsumer(CharBuffer csvBuffer) {
		this.csvBuffer = csvBuffer;
	}

	@Override
	public final void consumeAllBuffer(CellConsumer cellConsumer) {
		int bufferLength = csvBuffer.getBufferSize();
		char[] buffer = csvBuffer.getCharBuffer();
		for(int i = _currentIndex; i  < bufferLength; i++) {
			consumeOneChar(buffer[i], i, cellConsumer);
		}
		_currentIndex = bufferLength;
	}

	protected abstract void consumeOneChar(char c, int i, CellConsumer cellConsumer);

	/**
	 * use bit mask to testing if == IN_CR
	 */
	protected final void turnOffCrFlag() {
		currentState &= TURN_OFF_IN_CR_MASK;
	}

	protected final void newCellIfNotInQuote(int currentIndex, CellConsumer cellConsumer) {
		if (isInQuote())
			return;

		newCell(currentIndex, cellConsumer);
	}

	private boolean isInQuote() {
		return (currentState &  IN_QUOTE) != 0;
	}

	protected final boolean handleEndOfLineLF(int currentIndex, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			if ((currentState & IN_CR) == 0) {
				endOfRow(currentIndex, cellConsumer);
				return true;
			} else {
				// we had a preceding cr so shift the marl
				csvBuffer.mark(currentIndex + 1);
			}
		}
		return false;
	}

	protected final boolean handleEndOfLineCR(int currentIndex, CellConsumer cellConsumer) {
		if (!isInQuote()) {
			endOfRow(currentIndex, cellConsumer);
			currentState |= IN_CR;
			return true;
		}
		return false;
	}

	private void endOfRow(int currentIndex, CellConsumer cellConsumer) {
		newCell(currentIndex, cellConsumer);
		cellConsumer.endOfRow();
	}

	protected void quote(int currentIndex) {
		if (isAllConsumedFromMark(currentIndex)) {
			currentState |= IN_QUOTE;
		} else {
			currentState ^= ALL_QUOTES;
		}
	}

	protected void newCell(int currentIndex, CellConsumer cellConsumer) {
		char[] charBuffer = csvBuffer.getCharBuffer();
		int start = csvBuffer.getMark();
		int length = currentIndex - start;

		final char quoteChar = quoteChar();
		if (charBuffer[start] == quoteChar) {
			length = unescape(charBuffer, start, length, quoteChar);
			start++;
		}

		cellConsumer.newCell(charBuffer, start, length);
		csvBuffer.mark(currentIndex + 1);
		currentState = NONE;
	}

	@Override
	public final void finish(CellConsumer cellConsumer) {
		if (!isAllConsumedFromMark(_currentIndex)) {
			newCell(_currentIndex, cellConsumer);
		}
		cellConsumer.end();
	}

	private void shiftCurrentIndex(int mark) {
		_currentIndex -= mark;
	}

	@Override
	public final boolean refillBuffer() throws IOException {
		shiftCurrentIndex(csvBuffer.shiftBufferToMark());
		return csvBuffer.fillBuffer();
	}

	protected final boolean isAllConsumedFromMark(int bufferIndex) {
		return (bufferIndex) <  (csvBuffer.getMark() + 1)  ;
	}

	protected int unescape(final char[] chars, final int offset, final int length, char quoteChar) {
		int start = offset + 1;
		int shiftedIndex = start;
		boolean notEscaped = true;

		int lastCharacter = offset + length - 1;

		// copy chars apart from escape chars
		for(int i = start; i < lastCharacter; i++) {
			notEscaped = chars[i] != quoteChar || !notEscaped;
			if (notEscaped) {
				chars[shiftedIndex++] = chars[i];
			}
		}

		// if last is not quote add to shifted char
		if (chars[(lastCharacter)] != quoteChar || !notEscaped) {
			chars[shiftedIndex++] = chars[(lastCharacter)];
		}

		return shiftedIndex - start;
	}
}
