package gregtech.common.metatileentities.multiblock;

import gregtech.Bootstrap;
import gregtech.api.metatileentity.multiblock.IBatteryData;
import gregtech.api.util.random.XoShiRo256PlusPlusRandom;
import gregtech.common.metatileentities.multi.electric.MetaTileEntityPowerSubstation.PowerStationEnergyBank;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static gregtech.common.metatileentities.multi.electric.MetaTileEntityPowerSubstation.MAX_BATTERY_LAYERS;
import static gregtech.common.metatileentities.multi.electric.MetaTileEntityPowerSubstation.PASSIVE_DRAIN_DIVISOR;
import static gregtech.common.metatileentities.multi.electric.MetaTileEntityPowerSubstation.PASSIVE_DRAIN_MAX_PER_STORAGE;
import static org.hamcrest.Matchers.is;

public class PowerSubstationTest {

    @BeforeAll
    public static void bootstrap() {
        Bootstrap.perform();
    }

    @Test
    public void Test_1_Slot() {
        PowerStationEnergyBank storage = createStorage(100);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(100));

        // Random fill and drain tests
        MatcherAssert.assertThat(storage.fill(50), is(50L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(50));
        MatcherAssert.assertThat(storage.fill(100), is(50L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));
        MatcherAssert.assertThat(storage.fill(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));

        MatcherAssert.assertThat(storage.drain(50), is(50L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(50));
        MatcherAssert.assertThat(storage.drain(100), is(50L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Fully fill and drain
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));
        MatcherAssert.assertThat(storage.fill(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));

        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Try to overfill and overdrain
        MatcherAssert.assertThat(storage.fill(1000), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));

        MatcherAssert.assertThat(storage.drain(1000), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
    }

    @Test
    public void Test_4_Slot_Equal_Sizes() {
        PowerStationEnergyBank storage = createStorage(100, 100, 100, 100);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(400));

        // No overlap of slots
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(200));
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(300));
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));
        MatcherAssert.assertThat(storage.fill(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));

        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(300));
        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(200));
        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));
        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Overlap slots
        MatcherAssert.assertThat(storage.fill(150), is(150L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(150));
        MatcherAssert.assertThat(storage.fill(50), is(50L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(200));
        MatcherAssert.assertThat(storage.fill(200), is(200L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));
        MatcherAssert.assertThat(storage.fill(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));

        MatcherAssert.assertThat(storage.drain(150), is(150L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(250));
        MatcherAssert.assertThat(storage.drain(50), is(50L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(200));
        MatcherAssert.assertThat(storage.drain(200), is(200L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Fully fill and drain
        MatcherAssert.assertThat(storage.fill(400), is(400L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));
        MatcherAssert.assertThat(storage.fill(400), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));

        MatcherAssert.assertThat(storage.drain(400), is(400L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(400), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Try to overfill and overdrain
        MatcherAssert.assertThat(storage.fill(1000), is(400L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(400));

        MatcherAssert.assertThat(storage.drain(1000), is(400L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
    }

    @Test
    public void Test_4_Slot_Different_Sizes() {
        PowerStationEnergyBank storage = createStorage(100, 200, 300, 400);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(1000));

        // No overlap of slots
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));
        MatcherAssert.assertThat(storage.fill(200), is(200L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(300));
        MatcherAssert.assertThat(storage.fill(300), is(300L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(600));
        MatcherAssert.assertThat(storage.fill(400), is(400L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));
        MatcherAssert.assertThat(storage.fill(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));

        MatcherAssert.assertThat(storage.drain(400), is(400L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(600));
        MatcherAssert.assertThat(storage.drain(300), is(300L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(300));
        MatcherAssert.assertThat(storage.drain(200), is(200L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(100));
        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Overlap slots
        MatcherAssert.assertThat(storage.fill(200), is(200L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(200));
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(300));
        MatcherAssert.assertThat(storage.fill(600), is(600L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(900));
        MatcherAssert.assertThat(storage.fill(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));
        MatcherAssert.assertThat(storage.fill(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));

        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(900));
        MatcherAssert.assertThat(storage.drain(600), is(600L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(300));
        MatcherAssert.assertThat(storage.drain(100), is(100L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(200));
        MatcherAssert.assertThat(storage.drain(200), is(200L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(100), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Fully fill and drain
        MatcherAssert.assertThat(storage.fill(1000), is(1000L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));
        MatcherAssert.assertThat(storage.fill(1000), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));

        MatcherAssert.assertThat(storage.drain(1000), is(1000L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
        MatcherAssert.assertThat(storage.drain(1000), is(0L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Try to overfill and overdrain
        MatcherAssert.assertThat(storage.fill(10000), is(1000L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1000));

        MatcherAssert.assertThat(storage.drain(10000), is(1000L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));
    }

    @SuppressWarnings("NumericOverflow")
    @Test
    public void Test_Over_Long() {
        PowerStationEnergyBank storage = createStorage(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE));

        long halfLong = Long.MAX_VALUE / 2;

        MatcherAssert.assertThat(storage.fill(halfLong), is(halfLong));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(halfLong));
        MatcherAssert.assertThat(storage.fill(Long.MAX_VALUE), is(Long.MAX_VALUE));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(halfLong, Long.MAX_VALUE));

        MatcherAssert.assertThat(storage.drain(halfLong), is(halfLong));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(Long.MAX_VALUE));
        MatcherAssert.assertThat(storage.drain(Long.MAX_VALUE), is(Long.MAX_VALUE));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(0));

        // Test overflow
        Assertions.assertThrows(IllegalArgumentException.class, () -> storage.fill(Long.MAX_VALUE + 1000));
        Assertions.assertThrows(IllegalArgumentException.class, () -> storage.drain(Long.MAX_VALUE + 1000));
    }

    @Test
    public void Test_Rebuild_Storage() {
        PowerStationEnergyBank storage = createStorage(100, 500, 4000);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(4600));

        // Set up the storage with some amount of energy
        MatcherAssert.assertThat(storage.fill(3000), is(3000L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(3000));

        // Rebuild with more storage than needed
        rebuildStorage(storage, 1000, 4000, 4000);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(9000));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(3000));

        // Reset
        storage = createStorage(100, 500, 4000);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(4600));

        // Set up storage with energy again
        MatcherAssert.assertThat(storage.fill(3000), is(3000L));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(3000));

        // Rebuild with less storage than needed
        rebuildStorage(storage, 100, 100, 400, 500);
        MatcherAssert.assertThat(storage.getCapacity(), isBigInt(1100));
        MatcherAssert.assertThat(storage.getStored(), isBigInt(1100));
    }

    @Test
    public void Test_Optimized_Big_Integer_Summarize() {
        Consumer<XoShiRo256PlusPlusRandom> testRunner = r -> {
            BigInteger summation = BigInteger.ZERO;
            long[] storageValues = new long[9 * MAX_BATTERY_LAYERS];
            for (int i = 0; i < storageValues.length; i++) {
                long randomLong = Math.abs(r.nextLong());
                storageValues[i] = randomLong;
                summation = summation.add(BigInteger.valueOf(randomLong));
            }

            PowerStationEnergyBank storage = createStorage(storageValues);
            MatcherAssert.assertThat(storage.getCapacity(), is(summation));
        };

        for (int i = 0; i < 100; i++) {
            testRunner.accept(new XoShiRo256PlusPlusRandom());
        }
    }

    @Test
    public void Test_Passive_Drain_Calculation() {
        // 100kEU/t per storage block "too large" (like max long)
        PowerStationEnergyBank storage = createStorage(Long.MAX_VALUE, Long.MAX_VALUE);
        MatcherAssert.assertThat(storage.getPassiveDrainPerTick(),
                is(2 * PASSIVE_DRAIN_MAX_PER_STORAGE));

        Consumer<XoShiRo256PlusPlusRandom> testRunner = r -> {
            int numTruncated = 0;
            BigInteger nonTruncated = BigInteger.ZERO;

            long[] storageValues = new long[9 * MAX_BATTERY_LAYERS];
            for (int i = 0; i < storageValues.length; i++) {
                long randomLong = r.nextLong(PASSIVE_DRAIN_MAX_PER_STORAGE * PASSIVE_DRAIN_DIVISOR * 2);
                storageValues[i] = randomLong;
                if (randomLong / PASSIVE_DRAIN_DIVISOR >= PASSIVE_DRAIN_MAX_PER_STORAGE) {
                    numTruncated++;
                } else {
                    nonTruncated = nonTruncated.add(BigInteger.valueOf(randomLong));
                }
            }

            PowerStationEnergyBank testStorage = createStorage(storageValues);
            MatcherAssert.assertThat(testStorage.getPassiveDrainPerTick(),
                    is(nonTruncated.divide(BigInteger.valueOf(PASSIVE_DRAIN_DIVISOR))
                            .add(BigInteger.valueOf(numTruncated * PASSIVE_DRAIN_MAX_PER_STORAGE))
                            .longValue()));
        };

        for (int i = 0; i < 100; i++) {
            testRunner.accept(new XoShiRo256PlusPlusRandom());
        }
    }

    @Test
    public void Test_Fill_Drain_Randomized() {
        Consumer<XoShiRo256PlusPlusRandom> testRunner = r -> {
            BigInteger capacity = BigInteger.ZERO;
            long[] storageValues = new long[9 * MAX_BATTERY_LAYERS];
            for (int i = 0; i < storageValues.length; i++) {
                long randomLong = Math.abs(r.nextLong());
                storageValues[i] = randomLong;
                capacity = capacity.add(BigInteger.valueOf(randomLong));
            }

            PowerStationEnergyBank storage = createStorage(storageValues);

            // test capacity
            MatcherAssert.assertThat(storage.getCapacity(), is(capacity));

            BigInteger current = BigInteger.valueOf(Long.MAX_VALUE)
                    .multiply(BigInteger.valueOf(9 * MAX_BATTERY_LAYERS / 4));
            for (int i = 0; i < 9 * MAX_BATTERY_LAYERS / 4; i++) {
                storage.fill(Long.MAX_VALUE);
            }

            MatcherAssert.assertThat(storage.getStored(), is(current));

            for (int i = 0; i < 100; i++) {
                long randLong = Math.abs(r.nextLong());
                BigInteger randBig = BigInteger.valueOf(randLong);
                if (r.nextBoolean()) {
                    MatcherAssert.assertThat(storage.fill(randLong), is(randLong));
                    current = current.add(randBig);
                    MatcherAssert.assertThat(storage.getStored(), is(current));
                } else {
                    MatcherAssert.assertThat(storage.drain(randLong), is(randLong));
                    current = current.subtract(randBig);
                    MatcherAssert.assertThat(storage.getStored(), is(current));
                }
            }

            while (!current.equals(capacity)) {
                long randLong = Math.abs(r.nextLong());
                BigInteger randBig = BigInteger.valueOf(randLong);
                if (current.add(randBig).compareTo(capacity) >= 0) {
                    MatcherAssert.assertThat(storage.fill(randLong), is(capacity.subtract(current).longValue()));
                    current = capacity;
                } else {
                    MatcherAssert.assertThat(storage.fill(randLong), is(randLong));
                    current = current.add(randBig);
                }
                MatcherAssert.assertThat(storage.getStored(), is(current));
            }

            while (current.signum() > 0) {
                long randLong = Math.abs(r.nextLong());
                BigInteger randBig = BigInteger.valueOf(randLong);
                if (current.compareTo(randBig) <= 0) {
                    MatcherAssert.assertThat(storage.drain(randLong), is(current.longValue()));
                    current = BigInteger.ZERO;
                } else {
                    MatcherAssert.assertThat(storage.drain(randLong), is(randLong));
                    current = current.subtract(randBig);
                }
                MatcherAssert.assertThat(storage.getStored(), is(current));
            }
        };

        for (int i = 0; i < 100; i++) {
            testRunner.accept(new XoShiRo256PlusPlusRandom());
        }
    }

    private static Matcher<BigInteger> isBigInt(long value, long... additional) {
        BigInteger retVal = BigInteger.valueOf(value);
        if (additional != null) {
            for (long l : additional) {
                retVal = retVal.add(BigInteger.valueOf(l));
            }
        }
        return is(retVal);
    }

    private static PowerStationEnergyBank createStorage(long... storageValues) {
        List<IBatteryData> batteries = new ArrayList<>();
        for (long value : storageValues) {
            batteries.add(new TestBattery(value));
        }
        return new PowerStationEnergyBank(batteries);
    }

    private static void rebuildStorage(PowerStationEnergyBank storage, long... storageValues) {
        List<IBatteryData> batteries = new ArrayList<>();
        for (long value : storageValues) {
            batteries.add(new TestBattery(value));
        }
        storage.rebuild(batteries);
    }

    private static class TestBattery implements IBatteryData {

        private final long capacity;

        private TestBattery(long capacity) {
            this.capacity = capacity;
        }

        @Override
        public long getCapacity() {
            return capacity;
        }

        // not used in this test
        @Override
        public int getTier() {
            return 0;
        }

        // not used in this test
        @NotNull
        @Override
        public String getBatteryName() {
            return "";
        }
    }
}
