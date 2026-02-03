package com.ar.laboratory.baseapi2.infrastructure.adapter.in.web;

import com.ar.laboratory.baseapi2.application.dto.CreateExampleRequest;
import com.ar.laboratory.baseapi2.application.dto.ExampleResponse;
import com.ar.laboratory.baseapi2.application.port.in.CreateExampleUseCase;
import com.ar.laboratory.baseapi2.application.port.in.FindExampleByDniUseCase;
import com.ar.laboratory.baseapi2.application.port.in.ListExamplesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controller REST para Examples */
@Slf4j
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
@Tag(name = "Examples", description = "API para gestión de ejemplos")
public class ExampleController {

    private final CreateExampleUseCase createExampleUseCase;
    private final ListExamplesUseCase listExamplesUseCase;
    private final FindExampleByDniUseCase findExampleByDniUseCase;

    /** Crear un nuevo Example */
    @Operation(
            summary = "Crear un nuevo ejemplo",
            description = "Crea un nuevo ejemplo con nombre y DNI únicos")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Ejemplo creado exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ExampleResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Solicitud inválida - Error de validación o DNI duplicado"),
                @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @PostMapping
    public ResponseEntity<ExampleResponse> create(
            @Valid @RequestBody CreateExampleRequest request) {
        log.info("Request POST /examples: {}", request);

        ExampleResponse response = createExampleUseCase.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Listar todos los Examples */
    @Operation(
            summary = "Listar todos los ejemplos",
            description = "Obtiene la lista completa de todos los ejemplos registrados")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Lista obtenida exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ExampleResponse.class))),
                @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @GetMapping
    public ResponseEntity<List<ExampleResponse>> listAll() {
        log.info("Request GET /examples");

        List<ExampleResponse> response = listExamplesUseCase.listAll();

        return ResponseEntity.ok(response);
    }

    /** Buscar Example por DNI */
    @Operation(
            summary = "Buscar ejemplo por DNI",
            description =
                    "Busca un ejemplo específico por su DNI. Los resultados se cachean en Redis"
                            + " para mejorar el rendimiento")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ejemplo encontrado exitosamente",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ExampleResponse.class))),
                @ApiResponse(responseCode = "404", description = "Ejemplo no encontrado"),
                @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    @GetMapping("/dni/{dni}")
    public ResponseEntity<ExampleResponse> findByDni(@PathVariable String dni) {
        log.info("Request GET /examples/dni/{}", dni);

        ExampleResponse response = findExampleByDniUseCase.findByDni(dni);

        return ResponseEntity.ok(response);
    }
}
