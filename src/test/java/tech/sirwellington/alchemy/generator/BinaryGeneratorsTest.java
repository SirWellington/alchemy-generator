/*
 * Copyright 2015 SirWellington Tech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.sirwellington.alchemy.generator;

import java.nio.ByteBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.negativeIntegers;
import static tech.sirwellington.alchemy.generator.Throwables.assertThrows;

/**
 *
 * @author SirWellington
 */
@RunWith(MockitoJUnitRunner.class)
public class BinaryGeneratorsTest
{

    @Before
    public void setUp()
    {
    }

    @Test
    public void testCannotInstantiate()
    {
        System.out.println("testCannotInstantiate");

        assertThrows(() -> new BinaryGenerators())
                .isInstanceOf(IllegalAccessException.class);

        assertThrows(() -> BinaryGenerators.class.newInstance())
                .isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void testBinary()
    {
        System.out.println("testBinary");
        
        int bytes = integers(50, 5000).get();
        AlchemyGenerator<byte[]> instance = BinaryGenerators.binary(bytes);

        assertNotNull(instance);

        Tests.doInLoop(i -> 
        {
            byte[] value = instance.get();
            assertThat(value, notNullValue());
            assertThat(value.length, is(bytes));
        });
    }
    
    @Test
    public void testBinaryEdgeCases()
    {
        System.out.println("testBinaryGeneratorEdgeCases");
        
        
        AlchemyGenerator<byte[]> instance = BinaryGenerators.binary(0);
        assertThat(instance, notNullValue());
        byte[] result = instance.get();
        assertThat(result, notNullValue());
        assertThat(result.length, is(0));
        
        int length = one(negativeIntegers());
        assertThrows(() -> BinaryGenerators.binary(length))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testByteBuffers()
    {
        System.out.println("testByteBuffers");
        
        int size = one(integers(10, 1_000));
        AlchemyGenerator<ByteBuffer> instance = BinaryGenerators.byteBuffers(size);
        assertThat(instance, notNullValue());
        
        Tests.doInLoop(i ->
        {
            ByteBuffer result = instance.get();
            assertThat(result, notNullValue());
            assertThat(result.limit(), is(size));
            assertThat(result.array().length, is(size));
        });
    }
    
    @Test
    public void testByteBuffersEdgeCases()
    {
        System.out.println("testByteBuffersEdgeCases");
        
        int size = one(negativeIntegers());
        assertThrows(() -> BinaryGenerators.byteBuffers(size))
            .isInstanceOf(IllegalArgumentException.class);
        
        ByteBuffer result = BinaryGenerators.byteBuffers(0).get();
        assertThat(result, notNullValue());
        assertThat(result.limit(), is(0));
    }

    @Test
    public void testBytes()
    {
        System.out.println("testBytes");
        
        AlchemyGenerator<Byte> generator = BinaryGenerators.bytes();
        assertThat(generator, notNullValue());
        
        Tests.doInLoop(i -> 
        {
            Byte result = generator.get();
            assertThat(result, notNullValue());
        });
    }

}
