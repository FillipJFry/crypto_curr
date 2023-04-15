package com.goit.cryptocurr;

import java.io.Closeable;
import java.util.Iterator;

public interface IRecordsIterator extends Closeable, Iterator<CryptoCurrRecord> { }
