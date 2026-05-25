import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../domain/entities/sample_entity.dart';
import '../../domain/usecases/get_samples_usecase.dart';
import '../../data/datasources/sample_remote_datasource.dart';
import '../../data/repositories/sample_repository_impl.dart';
import '../../../../core/network/dio_client.dart';

part 'sample_provider.g.dart';

@riverpod
GetSamplesUseCase getSamplesUseCase(Ref ref) {
  final dio = ref.watch(dioClientProvider);
  final dataSource = SampleRemoteDataSourceImpl(dio);
  final repository = SampleRepositoryImpl(dataSource);
  return GetSamplesUseCase(repository);
}

@riverpod
Future<List<SampleEntity>> samples(Ref ref) {
  final useCase = ref.watch(getSamplesUseCaseProvider);
  return useCase();
}
