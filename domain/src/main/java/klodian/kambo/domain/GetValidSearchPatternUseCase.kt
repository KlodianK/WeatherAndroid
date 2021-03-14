package klodian.kambo.domain

import arrow.core.Either
import javax.inject.Inject

class GetValidSearchPatternUseCase @Inject constructor() {

    sealed class PatternValidationError {
        object NullOrEmptyPattern : PatternValidationError()
        object TooManyCommaParams : PatternValidationError()
        object NoParamsFound : PatternValidationError()
    }

    operator fun invoke(pattern: String?): Either<PatternValidationError, String> {
        if (pattern.isNullOrEmpty()) {
            return Either.left(PatternValidationError.NullOrEmptyPattern)
        } else {

            val meaningfulCommaParams = pattern
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (meaningfulCommaParams.isEmpty()) {
                return Either.left(PatternValidationError.NoParamsFound)
            }

            if (meaningfulCommaParams.size > 3) {
                return Either.left(PatternValidationError.TooManyCommaParams)
            }

            return Either.right(meaningfulCommaParams.joinToString(separator = ","))
        }
    }
}