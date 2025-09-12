package com.fleet.management.be.config

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class GraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {
    override fun resolveToSingleError(ex: Throwable, env: graphql.schema.DataFetchingEnvironment): GraphQLError? {
        val b = GraphqlErrorBuilder.newError(env).message(ex.message ?: "Unexpected error")
        return when (ex) {
            is IllegalArgumentException -> b.errorType(graphql.ErrorType.ValidationError).build()
            is ResponseStatusException -> b.errorType(graphql.ErrorType.ExecutionAborted).build()
            else -> b.errorType(graphql.ErrorType.DataFetchingException).build()
        }
    }
}
