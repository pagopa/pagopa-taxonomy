package it.gov.pagopa.project;

import com.microsoft.azure.functions.ExecutionContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TaxonomyTest {

    @Spy
    TaxonomyFunction function;

    @Mock
    ExecutionContext context;

}
