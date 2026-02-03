package com.ar.laboratory.baseapi2.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.ar.laboratory.baseapi2.example.application.outbound.port.ExampleRepositoryPort;
import com.ar.laboratory.baseapi2.example.application.usecase.ListExamplesUseCase;
import com.ar.laboratory.baseapi2.example.domain.model.Example;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListExamplesUseCase Tests")
class ListExamplesServiceTest {

    @Mock private ExampleRepositoryPort exampleRepositoryPort;

    @InjectMocks private ListExamplesUseCase listExamplesUseCase;

    @Test
    @DisplayName("Debe listar todos los Examples exitosamente")
    void shouldListAllExamplesSuccessfully() {
        // Given
        List<Example> examples =
                Arrays.asList(
                        Example.builder().id(1L).name("John Doe").dni("12345678").build(),
                        Example.builder().id(2L).name("Jane Smith").dni("87654321").build());

        when(exampleRepositoryPort.findAll()).thenReturn(examples);

        // When
        List<Example> responses = listExamplesUseCase.execute();

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("John Doe");
        assertThat(responses.get(1).getName()).isEqualTo("Jane Smith");

        verify(exampleRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay Examples")
    void shouldReturnEmptyListWhenNoExamples() {
        // Given
        when(exampleRepositoryPort.findAll()).thenReturn(Arrays.asList());

        // When
        List<Example> responses = listExamplesUseCase.execute();

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(exampleRepositoryPort, times(1)).findAll();
    }
}
